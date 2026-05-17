package com.sigi.servicio_media.controller;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.sigi.servicio_media.dto.MediaResponse;
import com.sigi.servicio_media.model.MediaArchivo;
import com.sigi.servicio_media.model.MediaArchivo.TipoMedia;
import com.sigi.servicio_media.service.MediaService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/media")
@Tag(name = "Media", description = "Subida y descarga de imágenes")
public class MediaController {

    private final MediaService mediaService;

    public MediaController(MediaService mediaService) {
        this.mediaService = mediaService;
    }

    @PostMapping(value = "/upload-registro", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Subir certificado de residencia (registro público)")
    public ResponseEntity<MediaResponse> uploadRegistro(
            @RequestParam("file") MultipartFile file) {
        try {
            MediaResponse resp = mediaService.subir(0L, file, TipoMedia.CERTIFICADO, null);
            resp.setUrl("/api/media/" + resp.getId() + "/archivo");
            return ResponseEntity.ok(resp);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Subir imagen (reporte o perfil)")
    public ResponseEntity<MediaResponse> upload(
            @RequestHeader("X-User-Id") Long usuarioId,
            @RequestParam("file") MultipartFile file,
            @RequestParam("tipo") TipoMedia tipo,
            @RequestParam(value = "referenciaId", required = false) Long referenciaId) {
        try {
            MediaResponse resp = mediaService.subir(usuarioId, file, tipo, referenciaId);
            resp.setUrl("/api/media/" + resp.getId() + "/archivo");
            return ResponseEntity.ok(resp);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}/vincular")
    @Operation(summary = "Vincular archivo a reporte o perfil")
    public ResponseEntity<MediaResponse> vincular(
            @RequestHeader("X-User-Id") Long usuarioId,
            @PathVariable Long id,
            @RequestParam TipoMedia tipo,
            @RequestParam Long referenciaId) {
        try {
            MediaResponse resp = mediaService.vincularReferencia(id, usuarioId, tipo, referenciaId);
            resp.setUrl("/api/media/" + resp.getId() + "/archivo");
            return ResponseEntity.ok(resp);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}/archivo")
    @Operation(summary = "Descargar archivo por ID")
    public ResponseEntity<Resource> descargar(@PathVariable Long id) {
        try {
            MediaResponse meta = toResponse(mediaService.obtenerMeta(id));
            Resource resource = mediaService.obtenerArchivo(id);
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(meta.getContentType()))
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "inline; filename=\"" + meta.getNombreOriginal() + "\"")
                    .body(resource);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Metadatos del archivo")
    public ResponseEntity<MediaResponse> meta(@PathVariable Long id) {
        try {
            MediaResponse r = toResponse(mediaService.obtenerMeta(id));
            return ResponseEntity.ok(r);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    private static MediaResponse toResponse(MediaArchivo archivo) {
        MediaResponse r = MediaResponse.fromEntity(archivo, "");
        r.setUrl("/api/media/" + r.getId() + "/archivo");
        return r;
    }
}
