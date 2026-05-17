package com.sigi.servicio_usuario.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ActualizarFotoRequest {

    @NotNull
    private Long fotoMediaId;
}
