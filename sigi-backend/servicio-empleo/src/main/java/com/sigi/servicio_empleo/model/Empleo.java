package com.sigi.servicio_empleo.model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "empleos")
@Data
@NoArgsConstructor
public class Empleo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String titulo;

    @Column(nullable = false, length = 120)
    private String departamento;

    @Column(nullable = false)
    private int plazas;

    @Column(nullable = false, length = 4000)
    private String descripcion;

    private LocalDate fechaCierre;

    @Column(nullable = false)
    private boolean activo = true;
}
