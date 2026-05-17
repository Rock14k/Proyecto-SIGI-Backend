package com.sigi.servicio_reporte.client.dto;

import lombok.Data;

@Data
public class EmergenciaFeignResponse {
    private Long id;
    private String estado;
    private String prioridad;
}
