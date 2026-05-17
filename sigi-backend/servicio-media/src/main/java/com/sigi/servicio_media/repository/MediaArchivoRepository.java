package com.sigi.servicio_media.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sigi.servicio_media.model.MediaArchivo;
import com.sigi.servicio_media.model.MediaArchivo.TipoMedia;

public interface MediaArchivoRepository extends JpaRepository<MediaArchivo, Long> {

    Optional<MediaArchivo> findByIdAndUsuarioId(Long id, Long usuarioId);

    Optional<MediaArchivo> findByTipoAndReferenciaId(TipoMedia tipo, Long referenciaId);
}
