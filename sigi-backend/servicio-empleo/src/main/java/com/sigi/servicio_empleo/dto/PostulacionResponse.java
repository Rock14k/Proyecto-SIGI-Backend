package com.sigi.servicio_empleo.dto;

import java.time.LocalDateTime;

import com.sigi.servicio_empleo.model.Postulacion;

import lombok.Data;

@Data
public class PostulacionResponse {

    private Long id;
    private Long empleoId;
    private Long usuarioId;
    private LocalDateTime fechaPostulacion;
    private String estado;
    private String postulanteNombre;
    private String postulanteApellido;
    private String postulanteEmail;
    private String postulanteRut;
    private String empleoTitulo;

    public static PostulacionResponse fromEntity(Postulacion p) {
        return fromEntity(p, null);
    }

    public static PostulacionResponse fromEntity(Postulacion p, String empleoTitulo) {
        PostulacionResponse r = new PostulacionResponse();
        r.setId(p.getId());
        r.setEmpleoId(p.getEmpleoId());
        r.setUsuarioId(p.getUsuarioId());
        r.setFechaPostulacion(p.getFechaPostulacion());
        r.setEstado(p.getEstado().name());
        r.setPostulanteNombre(p.getPostulanteNombre());
        r.setPostulanteApellido(p.getPostulanteApellido());
        r.setPostulanteEmail(p.getPostulanteEmail());
        r.setPostulanteRut(p.getPostulanteRut());
        r.setEmpleoTitulo(empleoTitulo);
        return r;
    }
}
