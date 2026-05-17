package com.sigi.servicio_reporte.dto;

import lombok.Data;

@Data
public class ValidarReporteRequest {

    private boolean aprobado;

    private String notasOperador;
}
