package com.sigi.servicio_reporte.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sigi.servicio_reporte.model.Reporte;
import com.sigi.servicio_reporte.model.Reporte.EstadoReporte;

public interface ReporteRepository extends JpaRepository<Reporte, Long> {

    List<Reporte> findByUsuarioIdOrderByFechaReporteDesc(Long usuarioId);

    List<Reporte> findByEstadoOrderByPrioridadDescFechaReporteAsc(EstadoReporte estado);

    List<Reporte> findAllByOrderByFechaReporteDesc();

    List<Reporte> findByEstadoNotOrderByPrioridadDescFechaReporteDesc(EstadoReporte estado);

    long countByDescripcionStartingWithAndFechaReporteAfter(String prefix, LocalDateTime fecha);

    long countByDireccionIgnoreCaseAndFechaReporteAfter(String direccion, LocalDateTime fecha);
}
