package com.sigi.servicio_reporte.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sigi.servicio_reporte.client.EmergenciaClient;
import com.sigi.servicio_reporte.client.UsuarioClient;
import com.sigi.servicio_reporte.client.dto.CoordenadasDto;
import com.sigi.servicio_reporte.client.dto.CrearEmergenciaFeignRequest;
import com.sigi.servicio_reporte.client.dto.UsuarioFeignDto;
import com.sigi.servicio_reporte.dto.ActualizarPrioridadRequest;
import com.sigi.servicio_reporte.dto.AsignarReporteRequest;
import com.sigi.servicio_reporte.dto.ClasificarReporteRequest;
import com.sigi.servicio_reporte.dto.CrearReporteRequest;
import com.sigi.servicio_reporte.dto.ReporteResponse;
import com.sigi.servicio_reporte.dto.ValidarReporteRequest;
import com.sigi.servicio_reporte.model.Reporte;
import com.sigi.servicio_reporte.model.Reporte.EstadoReporte;
import com.sigi.servicio_reporte.model.Reporte.PrioridadReporte;
import com.sigi.servicio_reporte.repository.ReporteRepository;
import com.sigi.servicio_reporte.service.PrioridadMetricasService.ResultadoPrioridad;

@Service
public class ReporteService {

    private static final Set<String> ROLES_OPERADOR = Set.of("ADMIN", "OPERADOR_MUNICIPAL");
    private static final Set<String> ROLES_EQUIPO = Set.of(
            "EQUIPO_EMERGENCIA",
            "BRIGADISTA",
            "BOMBERO",
            "AMBULANCIA",
            "SEGURIDAD_MUNICIPAL",
            "ADMIN",
            "OPERADOR_MUNICIPAL");
    private static final Set<String> ROLES_ASIGNABLES = Set.of(
            "EQUIPO_EMERGENCIA",
            "BRIGADISTA",
            "BOMBERO",
            "AMBULANCIA",
            "SEGURIDAD_MUNICIPAL");

    private final ReporteRepository reporteRepository;
    private final UbicacionConsultaService ubicacionConsultaService;
    private final EmergenciaClient emergenciaClient;
    private final PrioridadMetricasService prioridadMetricasService;
    private final UsuarioClient usuarioClient;

    public ReporteService(
            ReporteRepository reporteRepository,
            UbicacionConsultaService ubicacionConsultaService,
            EmergenciaClient emergenciaClient,
            PrioridadMetricasService prioridadMetricasService,
            UsuarioClient usuarioClient) {
        this.reporteRepository = reporteRepository;
        this.ubicacionConsultaService = ubicacionConsultaService;
        this.emergenciaClient = emergenciaClient;
        this.prioridadMetricasService = prioridadMetricasService;
        this.usuarioClient = usuarioClient;
    }

    @Transactional
    public ReporteResponse crearReporte(Long usuarioId, String rol, CrearReporteRequest req) {
        CoordenadasDto coords = ubicacionConsultaService.obtenerCoordenadas(req.getDireccion());

        ResultadoPrioridad metricas = prioridadMetricasService.calcular(req.getDescripcion(), req.getDireccion());
        PrioridadReporte prioridad = metricas.prioridad();
        if (ROLES_OPERADOR.contains(rol) && req.getPrioridad() != null && !req.getPrioridad().isBlank()) {
            prioridad = parsePrioridad(req.getPrioridad());
        }

        Reporte r = new Reporte();
        r.setUsuarioId(usuarioId);
        r.setDescripcion(req.getDescripcion());
        r.setDireccion(req.getDireccion());
        r.setLatitud(coords.getLatitud());
        r.setLongitud(coords.getLongitud());
        r.setEstado(EstadoReporte.PENDIENTE);
        r.setPrioridad(prioridad);
        r.setFechaReporte(LocalDateTime.now());
        r.setFotoMediaId(req.getFotoMediaId());
        r.setRequiereCarabineros(false);

        reporteRepository.save(r);
        return ReporteResponse.fromEntity(r, metricas.reportesSimilares(), metricas.motivoPrioridad());
    }

    public List<ReporteResponse> listarMisReportes(Long usuarioIdSolicitante, Long usuarioIdPath, String rol) {
        if (ROLES_OPERADOR.contains(rol) || ROLES_EQUIPO.contains(rol)) {
            return reporteRepository.findByUsuarioIdOrderByFechaReporteDesc(usuarioIdPath).stream()
                    .map(ReporteResponse::fromEntity)
                    .collect(Collectors.toList());
        }
        if (!usuarioIdSolicitante.equals(usuarioIdPath)) {
            throw new RuntimeException("No puede ver reportes de otro usuario");
        }
        return reporteRepository.findByUsuarioIdOrderByFechaReporteDesc(usuarioIdPath).stream()
                .map(ReporteResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public List<ReporteResponse> listarPendientes() {
        return reporteRepository.findByEstadoOrderByPrioridadDescFechaReporteAsc(EstadoReporte.PENDIENTE).stream()
                .map(ReporteResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public List<ReporteResponse> listarParaEquipo() {
        return reporteRepository.findByEstadoNotOrderByPrioridadDescFechaReporteDesc(EstadoReporte.RECHAZADO).stream()
                .map(ReporteResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public List<ReporteResponse> listarTodos() {
        return reporteRepository.findAllByOrderByFechaReporteDesc().stream()
                .map(ReporteResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public ReporteResponse validar(Long reporteId, Long operadorId, ValidarReporteRequest req) {
        Reporte r = reporteRepository.findById(reporteId)
                .orElseThrow(() -> new RuntimeException("Reporte no encontrado"));

        if (r.getEstado() != EstadoReporte.PENDIENTE) {
            throw new RuntimeException("Solo se validan reportes PENDIENTES");
        }

        if (req.isAprobado()) {
            r.setEstado(EstadoReporte.VALIDADO);
        } else {
            r.setEstado(EstadoReporte.RECHAZADO);
        }
        r.setFechaValidacion(LocalDateTime.now());
        r.setNotasOperador(req.getNotasOperador());
        r.setOperadorId(operadorId);
        reporteRepository.save(r);

        if (req.isAprobado()) {
            CrearEmergenciaFeignRequest emerg = new CrearEmergenciaFeignRequest();
            emerg.setReporteId(r.getId());
            emerg.setUsuarioReportanteId(r.getUsuarioId());
            emerg.setDescripcion(r.getDescripcion());
            emerg.setDireccion(r.getDireccion());
            emerg.setLatitud(r.getLatitud());
            emerg.setLongitud(r.getLongitud());
            emerg.setPrioridad(r.getPrioridad().name());
            emergenciaClient.crearDesdeReporte(emerg);
        }

        return ReporteResponse.fromEntity(r);
    }

    @Transactional
    public ReporteResponse actualizarPrioridad(Long reporteId, Long operadorId, ActualizarPrioridadRequest req) {
        Reporte r = reporteRepository.findById(reporteId)
                .orElseThrow(() -> new RuntimeException("Reporte no encontrado"));
        r.setPrioridad(parsePrioridad(req.getPrioridad()));
        if (req.getNotasOperador() != null) {
            r.setNotasOperador(req.getNotasOperador());
        }
        r.setOperadorId(operadorId);
        reporteRepository.save(r);
        return ReporteResponse.fromEntity(r);
    }

    @Transactional
    public ReporteResponse clasificar(Long reporteId, Long equipoId, ClasificarReporteRequest req) {
        Reporte r = reporteRepository.findById(reporteId)
                .orElseThrow(() -> new RuntimeException("Reporte no encontrado"));

        EstadoReporte nuevo = parseEstadoClasificacion(req.getEstado());
        r.setEstado(nuevo);
        r.setRequiereCarabineros(req.isRequiereCarabineros());
        if (req.getNotas() != null && !req.getNotas().isBlank()) {
            String prefijo = req.isRequiereCarabineros() ? "[CARABINEROS] " : "";
            r.setNotasOperador(prefijo + req.getNotas());
        }
        r.setOperadorId(equipoId);
        reporteRepository.save(r);
        return ReporteResponse.fromEntity(r);
    }

    @Transactional
    public ReporteResponse asignarAEquipo(Long reporteId, Long operadorId, AsignarReporteRequest req) {
        Reporte r = reporteRepository.findById(reporteId)
                .orElseThrow(() -> new RuntimeException("Reporte no encontrado"));

        UsuarioFeignDto destino = usuarioClient.obtenerPorId(req.getUsuarioId());
        if (destino == null || !destino.isActivo()) {
            throw new RuntimeException("Usuario de destino no disponible");
        }
        String rolDestino = destino.getRol();
        if (rolDestino == null || !ROLES_ASIGNABLES.contains(rolDestino)) {
            throw new RuntimeException("Solo se puede asignar a personal de emergencia");
        }

        r.setAsignadoAUsuarioId(destino.getId());
        r.setAsignadoARol(rolDestino);
        r.setAsignadoANombre(destino.getNombre() + " " + destino.getApellido());
        if (r.getEstado() == EstadoReporte.PENDIENTE || r.getEstado() == EstadoReporte.VALIDADO) {
            r.setEstado(EstadoReporte.EN_ATENCION);
        }
        if (req.getNotas() != null && !req.getNotas().isBlank()) {
            r.setNotasOperador("[ASIGNADO a " + rolDestino + "] " + req.getNotas());
        }
        r.setOperadorId(operadorId);
        reporteRepository.save(r);
        return ReporteResponse.fromEntity(r);
    }

    private static EstadoReporte parseEstadoClasificacion(String estado) {
        return switch (estado.toUpperCase()) {
            case "PENDIENTE" -> EstadoReporte.PENDIENTE;
            case "EN_ATENCION", "EN_PROCESO" -> EstadoReporte.EN_ATENCION;
            case "CERRADO", "RESUELTO" -> EstadoReporte.CERRADO;
            default -> throw new RuntimeException("Estado no válido: " + estado);
        };
    }

    private static PrioridadReporte parsePrioridad(String p) {
        try {
            return PrioridadReporte.valueOf(p.toUpperCase());
        } catch (Exception e) {
            return PrioridadReporte.MEDIA;
        }
    }
}
