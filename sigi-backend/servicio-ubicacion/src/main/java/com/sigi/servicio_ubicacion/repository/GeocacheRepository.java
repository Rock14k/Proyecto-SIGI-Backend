package com.sigi.servicio_ubicacion.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sigi.servicio_ubicacion.model.Geocache;

public interface GeocacheRepository extends JpaRepository<Geocache, Long> {

    Optional<Geocache> findByDireccionNormalizada(String direccionNormalizada);
}
