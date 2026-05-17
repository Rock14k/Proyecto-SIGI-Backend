package com.sigi.servicio_reporte.model;

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
@Table(name = "reportes")
@Data
@NoArgsConstructor
public class Reporte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long usuarioId;

    @Column(nullable = false, length = 4000)
    private String descripcion;

    @Column(nullable = false, length = 300)
    private String direccion;

    private Double latitud;
    private Double longitud;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoReporte estado;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PrioridadReporte prioridad;

    private LocalDateTime fechaReporte;
    private LocalDateTime fechaValidacion;

    @Column(length = 2000)
    private String notasOperador;

    private Long operadorId;

    /** ID en servicio-media (imagen del reporte) */
    private Long fotoMediaId;

    @Column(nullable = false)
    private boolean requiereCarabineros = false;

    /** Usuario de emergencia asignado por operador/admin */
    private Long asignadoAUsuarioId;

    @Column(length = 50)
    private String asignadoARol;

    @Column(length = 200)
    private String asignadoANombre;

    public enum EstadoReporte {
        PENDIENTE, EN_REVISION, VALIDADO, RECHAZADO, EN_ATENCION, CERRADO
    }

    public enum PrioridadReporte {
        BAJA, MEDIA, ALTA, CRITICA
    }
}
