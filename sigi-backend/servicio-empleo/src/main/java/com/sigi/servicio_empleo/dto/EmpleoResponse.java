package com.sigi.servicio_empleo.dto;

import java.time.LocalDate;

import com.sigi.servicio_empleo.model.Empleo;

import lombok.Data;

@Data
public class EmpleoResponse {

    private Long id;
    private String titulo;
    private String departamento;
    private int plazas;
    private String descripcion;
    private LocalDate fechaCierre;
    private boolean activo;

    public static EmpleoResponse fromEntity(Empleo e) {
        EmpleoResponse r = new EmpleoResponse();
        r.setId(e.getId());
        r.setTitulo(e.getTitulo());
        r.setDepartamento(e.getDepartamento());
        r.setPlazas(e.getPlazas());
        r.setDescripcion(e.getDescripcion());
        r.setFechaCierre(e.getFechaCierre());
        r.setActivo(e.isActivo());
        return r;
    }
}
