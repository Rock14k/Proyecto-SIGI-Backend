package com.sigi.servicio_emergencia.client.dto;

import lombok.Data;

@Data
public class AlertaEmergenciaFeignRequest {
    private Long emergenciaId;
    private String direccion;
    private String prioridad;
    private Long usuarioReportanteId;
}
