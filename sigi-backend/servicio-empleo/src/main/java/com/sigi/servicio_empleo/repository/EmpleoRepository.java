package com.sigi.servicio_empleo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sigi.servicio_empleo.model.Empleo;

public interface EmpleoRepository extends JpaRepository<Empleo, Long> {

    List<Empleo> findByActivoTrueOrderByFechaCierreAsc();
}
