package com.sigi.servicio_notificacion.service;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sigi.servicio_notificacion.dto.AlertaEmergenciaRequest;
import com.sigi.servicio_notificacion.dto.NotificacionResponse;
import com.sigi.servicio_notificacion.model.Notificacion;
import com.sigi.servicio_notificacion.model.Notificacion.TipoNotificacion;
import com.sigi.servicio_notificacion.repository.NotificacionRepository;

/**
 * MVP pedagógico: notificamos al reportante + IDs extra configurables (simula “vecinos”).
 * En producción filtraríamos por radio GPS usando la lat/lon de cada usuario.
 */
@Service
public class NotificacionService {

    private final NotificacionRepository notificacionRepository;

    /**
     * Lista separada por comas, ej. "1,2,3". Vacío si no hay extras.
     */
    @Value("${sigi.notificacion.usuarios-extra-por-config:}")
    private String usuariosExtraCsv;

    public NotificacionService(NotificacionRepository notificacionRepository) {
        this.notificacionRepository = notificacionRepository;
    }

    @Transactional
    public int crearAlertaPorEmergencia(AlertaEmergenciaRequest req) {
        Set<Long> destinatarios = new LinkedHashSet<>();
        destinatarios.add(req.getUsuarioReportanteId());
        destinatarios.addAll(parseIdsExtra(usuariosExtraCsv));

        String titulo = "Alerta de emergencia — Valle del Sol";
        String mensaje = String.format(
                "Incendio con prioridad %s en %s. Emergencia #%d. Mantenga calma y siga indicaciones oficiales.",
                req.getPrioridad(),
                req.getDireccion(),
                req.getEmergenciaId());

        int guardadas = 0;
        for (Long uid : destinatarios) {
            Notificacion n = new Notificacion();
            n.setUsuarioId(uid);
            n.setTitulo(titulo);
            n.setMensaje(mensaje);
            n.setTipo(TipoNotificacion.ALERTA_EMERGENCIA);
            n.setEmergenciaId(req.getEmergenciaId());
            notificacionRepository.save(n);
            guardadas++;
        }
        return guardadas;
    }

    static List<Long> parseIdsExtra(String csv) {
        if (csv == null || csv.isBlank()) {
            return List.of();
        }
        return Arrays.stream(csv.split(","))
                .map(String::strip)
                .filter(s -> !s.isEmpty())
                .mapToLong(Long::parseLong)
                .boxed()
                .toList();
    }

    public List<NotificacionResponse> listarPorUsuario(Long usuarioId) {
        return notificacionRepository.findByUsuarioIdOrderByFechaCreacionDesc(usuarioId).stream()
                .map(NotificacionResponse::fromEntity)
                .collect(Collectors.toList());
    }
}
