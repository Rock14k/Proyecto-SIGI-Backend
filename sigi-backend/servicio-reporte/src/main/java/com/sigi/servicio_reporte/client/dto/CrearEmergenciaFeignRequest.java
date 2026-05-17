package com.sigi.servicio_reporte.client.dto;

import lombok.Data;

@Data
public class CrearEmergenciaFeignRequest {
    private Long reporteId;
    private Long usuarioReportanteId;
    private String descripcion;
    private String direccion;
    private Double latitud;
    private Double longitud;
    private String prioridad;
}
