package com.sigi.servicio_recurso.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "recursos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Recurso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String nombre;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoRecurso tipo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoRecurso estado;

    private Long emergenciaId;

    private String descripcion;

    private Integer capacidad;

    @Column(length = 50)
    private String identificador;

    public enum TipoRecurso {
        CAMION_CISTERNA,
        CAMION_ESCALA,
        AMBULANCIA,
        BRIGADA_TERRESTRES,
        HELICOPTERO,
        UNIDAD_MANDO
    }

    public enum EstadoRecurso {
        DISPONIBLE,
        ASIGNADO,
        EN_RUTA,
        EN_OPERACION,
        EN_MANTENIMIENTO,
        FUERA_DE_SERVICIO
    }
}
