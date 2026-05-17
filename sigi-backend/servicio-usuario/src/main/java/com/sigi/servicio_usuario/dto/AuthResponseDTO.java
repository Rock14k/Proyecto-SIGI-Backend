package com.sigi.servicio_usuario.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponseDTO {

    private String token;
    private String tipoToken;
    private String email;
    private String rol;
    private Long usuarioId;
    private Long expiracionEnSegundos;
    private String nombre;
    private String apellido;
}
