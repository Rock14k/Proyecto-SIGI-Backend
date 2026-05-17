package com.sigi.servicio_reporte.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AsignarReporteRequest {

    @NotNull
    private Long usuarioId;

    private String notas;
}
