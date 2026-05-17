package com.sigi.servicio_reporte.client.dto;

import lombok.Data;

@Data
public class UsuarioFeignDto {

    private Long id;
    private String nombre;
    private String apellido;
    private String email;
    private String rol;
    private boolean activo;
}
