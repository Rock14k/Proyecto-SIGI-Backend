package com.sigi.servicio_emergencia.client.dto;

import lombok.Data;

@Data
public class AsignacionRecursoFeignRequest {
    private Long emergenciaId;
    private String prioridad;
}
