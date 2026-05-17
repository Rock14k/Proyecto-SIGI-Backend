package com.sigi.servicio_empleo.model;

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
@Table(name = "postulaciones")
@Data
@NoArgsConstructor
public class Postulacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long empleoId;

    @Column(nullable = false)
    private Long usuarioId;

    @Column(length = 100)
    private String postulanteNombre;

    @Column(length = 100)
    private String postulanteApellido;

    @Column(length = 150)
    private String postulanteEmail;

    @Column(length = 20)
    private String postulanteRut;

    private LocalDateTime fechaPostulacion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoPostulacion estado = EstadoPostulacion.ENVIADA;

    public enum EstadoPostulacion {
        ENVIADA, EN_REVISION, ACEPTADA, RECHAZADA
    }
}
