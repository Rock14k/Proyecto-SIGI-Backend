package com.sigi.servicio_emergencia.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sigi.servicio_emergencia.client.NotificacionFeignClient;
import com.sigi.servicio_emergencia.client.RecursoFeignClient;
import com.sigi.servicio_emergencia.client.dto.AlertaEmergenciaFeignRequest;
import com.sigi.servicio_emergencia.client.dto.AsignacionRecursoFeignRequest;
import com.sigi.servicio_emergencia.dto.ActualizarEstadoRequest;
import com.sigi.servicio_emergencia.dto.CrearEmergenciaRequest;
import com.sigi.servicio_emergencia.dto.EmergenciaResponse;
import com.sigi.servicio_emergencia.model.Emergencia;
import com.sigi.servicio_emergencia.model.Emergencia.EstadoEmergencia;
import com.sigi.servicio_emergencia.model.Emergencia.PrioridadIncendio;
import com.sigi.servicio_emergencia.repository.EmergenciaRepository;

@Service
public class EmergenciaService {

    private static final Logger log = LoggerFactory.getLogger(EmergenciaService.class);

    private final EmergenciaRepository emergenciaRepository;
    private final RecursoFeignClient recursoFeignClient;
    private final NotificacionFeignClient notificacionFeignClient;

    public EmergenciaService(
            EmergenciaRepository emergenciaRepository,
            RecursoFeignClient recursoFeignClient,
            NotificacionFeignClient notificacionFeignClient) {
        this.emergenciaRepository = emergenciaRepository;
        this.recursoFeignClient = recursoFeignClient;
        this.notificacionFeignClient = notificacionFeignClient;
    }

    @Transactional
    public EmergenciaResponse crearDesdeReporteValidado(CrearEmergenciaRequest req) {
        Emergencia e = new Emergencia();
        e.setReporteId(req.getReporteId());
        e.setDescripcion(req.getDescripcion());
        e.setDireccion(req.getDireccion());
        e.setLatitud(req.getLatitud());
        e.setLongitud(req.getLongitud());
        e.setEstado(EstadoEmergencia.ACTIVA);
        e.setPrioridad(parsePrioridad(req.getPrioridad()));
        e.setFechaCreacion(LocalDateTime.now());
        emergenciaRepository.save(e);

        try {
            AsignacionRecursoFeignRequest asign = new AsignacionRecursoFeignRequest();
            asign.setEmergenciaId(e.getId());
            asign.setPrioridad(req.getPrioridad());
            recursoFeignClient.asignar(asign);
        } catch (Exception ex) {
            log.error("No se pudo asignar recursos vía Feign: {}", ex.getMessage());
        }

        try {
            AlertaEmergenciaFeignRequest alert = new AlertaEmergenciaFeignRequest();
            alert.setEmergenciaId(e.getId());
            alert.setDireccion(req.getDireccion() != null ? req.getDireccion() : "Ubicación no detallada");
            alert.setPrioridad(req.getPrioridad());
            alert.setUsuarioReportanteId(req.getUsuarioReportanteId());
            notificacionFeignClient.crearAlerta(alert);
        } catch (Exception ex) {
            log.error("No se pudo registrar alertas: {}", ex.getMessage());
        }

        return EmergenciaResponse.fromEntity(e);
    }

    private static PrioridadIncendio parsePrioridad(String p) {
        try {
            return PrioridadIncendio.valueOf(p.toUpperCase());
        } catch (Exception e) {
            return PrioridadIncendio.MEDIA;
        }
    }

    public List<EmergenciaResponse> listarActivas() {
        return emergenciaRepository
                .findByEstadoInOrderByFechaCreacionDesc(
                        List.of(EstadoEmergencia.ACTIVA, EstadoEmergencia.EN_PROCESO, EstadoEmergencia.CONTROLADA))
                .stream()
                .map(EmergenciaResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public List<EmergenciaResponse> listarTodas() {
        return emergenciaRepository.findAll().stream()
                .map(EmergenciaResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public EmergenciaResponse actualizarEstado(Long id, ActualizarEstadoRequest req) {
        Emergencia e = emergenciaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Emergencia no encontrada: " + id));

        EstadoEmergencia nuevo = EstadoEmergencia.valueOf(req.getEstado().toUpperCase());
        e.setEstado(nuevo);

        if (req.getNotas() != null && !req.getNotas().isBlank()) {
            e.setNotas(req.getNotas());
        }

        if (nuevo == EstadoEmergencia.EN_PROCESO && e.getFechaInicioAtencion() == null) {
            e.setFechaInicioAtencion(LocalDateTime.now());
        }
        if (nuevo == EstadoEmergencia.RESUELTA || nuevo == EstadoEmergencia.CANCELADA) {
            e.setFechaResolucion(LocalDateTime.now());
            try {
                recursoFeignClient.liberar(id);
            } catch (Exception ex) {
                log.warn("Liberar recursos falló: {}", ex.getMessage());
            }
        }

        emergenciaRepository.save(e);
        return EmergenciaResponse.fromEntity(e);
    }
}
