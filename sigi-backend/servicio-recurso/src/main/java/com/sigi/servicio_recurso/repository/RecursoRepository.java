package com.sigi.servicio_recurso.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sigi.servicio_recurso.model.Recurso;
import com.sigi.servicio_recurso.model.Recurso.EstadoRecurso;
import com.sigi.servicio_recurso.model.Recurso.TipoRecurso;

public interface RecursoRepository extends JpaRepository<Recurso, Long> {

    List<Recurso> findByEstadoOrderById(EstadoRecurso estado);

    List<Recurso> findByEstadoAndTipoInOrderById(EstadoRecurso estado, Collection<TipoRecurso> tipos);

    List<Recurso> findByEmergenciaId(Long emergenciaId);
}
