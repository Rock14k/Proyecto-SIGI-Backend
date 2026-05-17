package com.sigi.servicio_reporte.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CrearReporteRequest {

    @NotBlank
    private String descripcion;

    @NotBlank
    private String direccion;

    /** Solo operador municipal puede fijar prioridad manualmente */
    private String prioridad;

    private Long fotoMediaId;
}
