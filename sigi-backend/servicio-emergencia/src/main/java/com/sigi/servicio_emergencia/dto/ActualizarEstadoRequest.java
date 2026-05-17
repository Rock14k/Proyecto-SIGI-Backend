package com.sigi.servicio_emergencia.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ActualizarEstadoRequest {

    @NotBlank
    private String estado;

    private String notas;
}
