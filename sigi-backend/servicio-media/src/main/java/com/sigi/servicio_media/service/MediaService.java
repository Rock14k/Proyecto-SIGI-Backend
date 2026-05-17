package com.sigi.servicio_media.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.sigi.servicio_media.dto.MediaResponse;
import com.sigi.servicio_media.model.MediaArchivo;
import com.sigi.servicio_media.model.MediaArchivo.TipoMedia;
import com.sigi.servicio_media.repository.MediaArchivoRepository;

import jakarta.annotation.PostConstruct;

@Service
public class MediaService {

    private final MediaArchivoRepository repository;

    @Value("${sigi.media.upload-dir:/app/uploads}")
    private String uploadDir;

    public MediaService(MediaArchivoRepository repository) {
        this.repository = repository;
    }

    @PostConstruct
    void initDir() throws IOException {
        Files.createDirectories(Paths.get(uploadDir));
    }

    @Transactional
    public MediaResponse subir(Long usuarioId, MultipartFile file, TipoMedia tipo, Long referenciaId) {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("Archivo vacío");
        }
        String original = file.getOriginalFilename() != null ? file.getOriginalFilename() : "archivo";
        String ext = "";
        int dot = original.lastIndexOf('.');
        if (dot > 0) {
            ext = original.substring(dot);
        }
        String stored = UUID.randomUUID() + ext;
        Path destino = Paths.get(uploadDir, stored);
        try {
            file.transferTo(destino);
        } catch (IOException e) {
            throw new RuntimeException("No se pudo guardar el archivo", e);
        }

        MediaArchivo m = new MediaArchivo();
        m.setUsuarioId(usuarioId);
        m.setTipo(tipo);
        m.setReferenciaId(referenciaId);
        m.setNombreOriginal(original);
        m.setRutaAlmacenamiento(destino.toString());
        m.setContentType(file.getContentType() != null ? file.getContentType() : "application/octet-stream");
        m.setFechaSubida(LocalDateTime.now());
        MediaArchivo guardado = repository.save(m);
        return MediaResponse.fromEntity(guardado, "");
    }

    public Resource obtenerArchivo(Long id) {
        MediaArchivo m = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Archivo no encontrado"));
        try {
            Path path = Paths.get(m.getRutaAlmacenamiento());
            Resource resource = new UrlResource(path.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                throw new RuntimeException("Archivo no legible");
            }
            return resource;
        } catch (Exception e) {
            throw new RuntimeException("Error al leer archivo", e);
        }
    }

    public MediaArchivo obtenerMeta(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Archivo no encontrado"));
    }

    @Transactional
    public MediaResponse vincularReferencia(Long mediaId, Long usuarioId, TipoMedia tipo, Long referenciaId) {
        MediaArchivo m = repository.findById(mediaId)
                .orElseThrow(() -> new RuntimeException("Media no encontrado"));
        if (!m.getUsuarioId().equals(usuarioId)) {
            throw new RuntimeException("No autorizado");
        }
        m.setTipo(tipo);
        m.setReferenciaId(referenciaId);
        return MediaResponse.fromEntity(repository.save(m), "");
    }
}
