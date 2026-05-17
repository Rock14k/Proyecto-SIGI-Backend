package com.sigi.servicio_notificacion.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sigi.servicio_notificacion.model.Notificacion;

public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {

    List<Notificacion> findByUsuarioIdOrderByFechaCreacionDesc(Long usuarioId);
}
