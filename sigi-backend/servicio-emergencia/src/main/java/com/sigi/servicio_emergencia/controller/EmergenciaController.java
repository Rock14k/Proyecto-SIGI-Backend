package com.sigi.servicio_emergencia.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sigi.servicio_emergencia.dto.ActualizarEstadoRequest;
import com.sigi.servicio_emergencia.dto.CrearEmergenciaRequest;
import com.sigi.servicio_emergencia.dto.EmergenciaResponse;
import com.sigi.servicio_emergencia.service.EmergenciaService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/emergencias")
@Tag(name = "Emergencias", description = "Ciclo de vida de emergencias urbanas")
public class EmergenciaController {

    private final EmergenciaService emergenciaService;

    public EmergenciaController(EmergenciaService emergenciaService) {
        this.emergenciaService = emergenciaService;
    }

    @PostMapping("/desde-reporte")
    @Operation(summary = "Crear emergencia cuando un operador valida un reporte")
    public ResponseEntity<EmergenciaResponse> desdeReporte(@Valid @RequestBody CrearEmergenciaRequest body) {
        return ResponseEntity.ok(emergenciaService.crearDesdeReporteValidado(body));
    }

    @GetMapping("/activas")
    @Operation(summary = "Emergencias no cerradas")
    public ResponseEntity<List<EmergenciaResponse>> activas() {
        return ResponseEntity.ok(emergenciaService.listarActivas());
    }

    @GetMapping
    @Operation(summary = "Listado completo (operador/admin)")
    public ResponseEntity<List<EmergenciaResponse>> todas() {
        return ResponseEntity.ok(emergenciaService.listarTodas());
    }

    @PutMapping("/{id}/estado")
    @Operation(summary = "Actualizar progreso (equipo de emergencia)")
    public ResponseEntity<EmergenciaResponse> estado(
            @PathVariable Long id,
            @Valid @RequestBody ActualizarEstadoRequest body) {
        return ResponseEntity.ok(emergenciaService.actualizarEstado(id, body));
    }
}
