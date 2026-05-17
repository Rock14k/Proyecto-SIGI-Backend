package com.sigi.servicio_reporte.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ActualizarPrioridadRequest {

    @NotBlank
    private String prioridad;

    private String notasOperador;
}
