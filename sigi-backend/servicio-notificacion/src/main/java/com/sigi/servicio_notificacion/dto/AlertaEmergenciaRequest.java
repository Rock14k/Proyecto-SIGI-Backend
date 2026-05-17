package com.sigi.servicio_notificacion.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AlertaEmergenciaRequest {

    @NotNull
    private Long emergenciaId;

    @NotBlank
    private String direccion;

    @NotBlank
    private String prioridad;

    /** Ciudadano que hizo el reporte original (recibe copia explícita) */
    @NotNull
    private Long usuarioReportanteId;
}
