package com.sigi.servicio_emergencia.client.dto;

import java.util.List;

import lombok.Data;

@Data
public class AsignacionRecursoFeignResponse {
    private List<Long> recursosAsignadosIds;
    private String mensaje;
}
