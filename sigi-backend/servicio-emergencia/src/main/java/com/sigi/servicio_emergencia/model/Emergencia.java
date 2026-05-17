package com.sigi.servicio_emergencia.model;

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
@Table(name = "emergencias")
@Data
@NoArgsConstructor
public class Emergencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long reporteId;

    @Column(nullable = false, length = 2000)
    private String descripcion;

    private String direccion;
    private Double latitud;
    private Double longitud;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoEmergencia estado;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PrioridadIncendio prioridad;

    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaInicioAtencion;
    private LocalDateTime fechaResolucion;

    @Column(length = 4000)
    private String notas;

    public enum EstadoEmergencia {
        ACTIVA, EN_PROCESO, CONTROLADA, RESUELTA, CANCELADA
    }

    public enum PrioridadIncendio {
        BAJA, MEDIA, ALTA, CRITICA
    }
}
