package com.sigi.servicio_notificacion.dto;

import java.time.LocalDateTime;

import com.sigi.servicio_notificacion.model.Notificacion;

import lombok.Data;

@Data
public class NotificacionResponse {

    private Long id;
    private Long usuarioId;
    private String titulo;
    private String mensaje;
    private String tipo;
    private Long emergenciaId;
    private LocalDateTime fechaCreacion;

    public static NotificacionResponse fromEntity(Notificacion n) {
        NotificacionResponse r = new NotificacionResponse();
        r.setId(n.getId());
        r.setUsuarioId(n.getUsuarioId());
        r.setTitulo(n.getTitulo());
        r.setMensaje(n.getMensaje());
        r.setTipo(n.getTipo().name());
        r.setEmergenciaId(n.getEmergenciaId());
        r.setFechaCreacion(n.getFechaCreacion());
        return r;
    }
}
