package com.sigi.servicio_reporte.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ClasificarReporteRequest {

    @NotBlank
    private String estado;

    private boolean requiereCarabineros;

    private String notas;
}
