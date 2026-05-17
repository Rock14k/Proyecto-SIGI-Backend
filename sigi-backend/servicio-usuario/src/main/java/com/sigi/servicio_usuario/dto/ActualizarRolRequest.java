package com.sigi.servicio_usuario.dto;

import com.sigi.servicio_usuario.model.Usuario;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ActualizarRolRequest {

    @NotNull
    private Usuario.Rol rol;
}
