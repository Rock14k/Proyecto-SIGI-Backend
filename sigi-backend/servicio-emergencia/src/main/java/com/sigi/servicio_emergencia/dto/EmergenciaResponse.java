package com.sigi.servicio_emergencia.dto;

import java.time.LocalDateTime;

import com.sigi.servicio_emergencia.model.Emergencia;

import lombok.Data;

@Data
public class EmergenciaResponse {

    private Long id;
    private Long reporteId;
    private String descripcion;
    private String direccion;
    private Double latitud;
    private Double longitud;
    private String estado;
    private String prioridad;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaInicioAtencion;
    private LocalDateTime fechaResolucion;
    private String notas;

    public static EmergenciaResponse fromEntity(Emergencia e) {
        EmergenciaResponse r = new EmergenciaResponse();
        r.setId(e.getId());
        r.setReporteId(e.getReporteId());
        r.setDescripcion(e.getDescripcion());
        r.setDireccion(e.getDireccion());
        r.setLatitud(e.getLatitud());
        r.setLongitud(e.getLongitud());
        r.setEstado(e.getEstado().name());
        r.setPrioridad(e.getPrioridad().name());
        r.setFechaCreacion(e.getFechaCreacion());
        r.setFechaInicioAtencion(e.getFechaInicioAtencion());
        r.setFechaResolucion(e.getFechaResolucion());
        r.setNotas(e.getNotas());
        return r;
    }
}
