package com.sigi.servicio_recurso.dto;

import com.sigi.servicio_recurso.model.Recurso;

import lombok.Data;

@Data
public class RecursoResponse {

    private Long id;
    private String nombre;
    private String tipo;
    private String estado;
    private Long emergenciaId;
    private String identificador;

    public static RecursoResponse fromEntity(Recurso r) {
        RecursoResponse dto = new RecursoResponse();
        dto.setId(r.getId());
        dto.setNombre(r.getNombre());
        dto.setTipo(r.getTipo().name());
        dto.setEstado(r.getEstado().name());
        dto.setEmergenciaId(r.getEmergenciaId());
        dto.setIdentificador(r.getIdentificador());
        return dto;
    }
}
