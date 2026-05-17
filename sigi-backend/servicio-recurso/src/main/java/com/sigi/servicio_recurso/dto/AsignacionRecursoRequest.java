package com.sigi.servicio_recurso.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AsignacionRecursoRequest {

    @NotNull
    private Long emergenciaId;

    /**
     * Misma escala que el informe: BAJA, MEDIA, ALTA, CRITICA
     */
    @NotNull
    private String prioridad;
}
