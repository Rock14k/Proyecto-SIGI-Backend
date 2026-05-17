package com.sigi.servicio_emergencia.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CrearEmergenciaRequest {

    @NotNull
    private Long reporteId;

    @NotNull
    private Long usuarioReportanteId;

    @NotNull
    private String descripcion;

    private String direccion;
    private Double latitud;
    private Double longitud;

    @NotNull
    private String prioridad;
}
