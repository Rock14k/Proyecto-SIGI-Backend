package com.sigi.servicio_media.dto;

import com.sigi.servicio_media.model.MediaArchivo;

import lombok.Data;

@Data
public class MediaResponse {

    private Long id;
    private String nombreOriginal;
    private String contentType;
    private String tipo;
    private Long referenciaId;
    private String url;

    public static MediaResponse fromEntity(MediaArchivo m, String baseUrl) {
        MediaResponse r = new MediaResponse();
        r.setId(m.getId());
        r.setNombreOriginal(m.getNombreOriginal());
        r.setContentType(m.getContentType());
        r.setTipo(m.getTipo().name());
        r.setReferenciaId(m.getReferenciaId());
        r.setUrl(baseUrl + "/api/media/" + m.getId() + "/archivo");
        return r;
    }
}
