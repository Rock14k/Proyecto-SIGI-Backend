package com.sigi.servicio_empleo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sigi.servicio_empleo.model.Postulacion;

public interface PostulacionRepository extends JpaRepository<Postulacion, Long> {

    List<Postulacion> findByUsuarioIdOrderByFechaPostulacionDesc(Long usuarioId);

    Optional<Postulacion> findByEmpleoIdAndUsuarioId(Long empleoId, Long usuarioId);

    List<Postulacion> findAllByOrderByFechaPostulacionDesc();
}
