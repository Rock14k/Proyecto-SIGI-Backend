package com.sigi.servicio_ubicacion.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Caché local: misma dirección no vuelve a consumir cuota de OpenCage.
 */
@Entity
@Table(name = "geocache_ubicaciones")
@Data
@NoArgsConstructor
public class Geocache {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Dirección normalizada (minúsculas, sin espacios duplicados) usada como clave */
    @Column(nullable = false, unique = true, length = 500)
    private String direccionNormalizada;

    private Double latitud;

    private Double longitud;

    public Geocache(String direccionNormalizada, Double latitud, Double longitud) {
        this.direccionNormalizada = direccionNormalizada;
        this.latitud = latitud;
        this.longitud = longitud;
    }
}
