package com.sigi.servicio_empleo.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class EmpleoRequest {

    @NotBlank
    private String titulo;

    @NotBlank
    private String departamento;

    @Min(1)
    private int plazas;

    @NotBlank
    private String descripcion;

    private LocalDate fechaCierre;
}
