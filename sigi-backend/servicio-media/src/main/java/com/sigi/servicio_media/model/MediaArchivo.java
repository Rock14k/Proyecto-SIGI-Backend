package com.sigi.servicio_media.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "media_archivos")
@Data
@NoArgsConstructor
public class MediaArchivo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long usuarioId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoMedia tipo;

    private Long referenciaId;

    @Column(nullable = false, length = 255)
    private String nombreOriginal;

    @Column(nullable = false, length = 500)
    private String rutaAlmacenamiento;

    @Column(nullable = false, length = 120)
    private String contentType;

    private LocalDateTime fechaSubida;

    public enum TipoMedia {
        REPORTE, PERFIL, CERTIFICADO
    }
}
