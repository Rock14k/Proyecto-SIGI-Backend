package com.sigi.servicio_reporte.dto;

import java.time.LocalDateTime;

import com.sigi.servicio_reporte.model.Reporte;

import lombok.Data;

@Data
public class ReporteResponse {

    private Long id;
    private Long usuarioId;
    private String descripcion;
    private String direccion;
    private Double latitud;
    private Double longitud;
    private String estado;
    private String prioridad;
    private LocalDateTime fechaReporte;
    private String notasOperador;
    private Long fotoMediaId;
    private String fotoUrl;
    private boolean requiereCarabineros;
    private int reportesSimilares;
    private String motivoPrioridad;
    private Long asignadoAUsuarioId;
    private String asignadoARol;
    private String asignadoANombre;

    public static ReporteResponse fromEntity(Reporte r) {
        return fromEntity(r, 0, null);
    }

    public static ReporteResponse fromEntity(Reporte r, int similares, String motivo) {
        ReporteResponse dto = new ReporteResponse();
        dto.setId(r.getId());
        dto.setUsuarioId(r.getUsuarioId());
        dto.setDescripcion(r.getDescripcion());
        dto.setDireccion(r.getDireccion());
        dto.setLatitud(r.getLatitud());
        dto.setLongitud(r.getLongitud());
        dto.setEstado(r.getEstado().name());
        dto.setPrioridad(r.getPrioridad().name());
        dto.setFechaReporte(r.getFechaReporte());
        dto.setNotasOperador(r.getNotasOperador());
        dto.setFotoMediaId(r.getFotoMediaId());
        if (r.getFotoMediaId() != null) {
            dto.setFotoUrl("/api/media/" + r.getFotoMediaId() + "/archivo");
        }
        dto.setRequiereCarabineros(r.isRequiereCarabineros());
        dto.setReportesSimilares(similares);
        dto.setMotivoPrioridad(motivo);
        dto.setAsignadoAUsuarioId(r.getAsignadoAUsuarioId());
        dto.setAsignadoARol(r.getAsignadoARol());
        dto.setAsignadoANombre(r.getAsignadoANombre());
        return dto;
    }
}
