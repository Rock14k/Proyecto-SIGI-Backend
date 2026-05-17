package com.sigi.servicio_recurso.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AsignacionRecursoResponse {

    private List<Long> recursosAsignadosIds;
    private String mensaje;
}
