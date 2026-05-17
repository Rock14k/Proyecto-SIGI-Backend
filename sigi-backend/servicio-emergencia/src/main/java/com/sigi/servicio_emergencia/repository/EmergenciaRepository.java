package com.sigi.servicio_emergencia.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sigi.servicio_emergencia.model.Emergencia;
import com.sigi.servicio_emergencia.model.Emergencia.EstadoEmergencia;

public interface EmergenciaRepository extends JpaRepository<Emergencia, Long> {

    List<Emergencia> findByEstadoInOrderByFechaCreacionDesc(List<EstadoEmergencia> estados);
}
