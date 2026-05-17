package com.sigi.servicio_recurso.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sigi.servicio_recurso.dto.AsignacionRecursoRequest;
import com.sigi.servicio_recurso.dto.AsignacionRecursoResponse;
import com.sigi.servicio_recurso.dto.RecursoResponse;
import com.sigi.servicio_recurso.service.RecursoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/recursos")
@Tag(name = "Recursos", description = "Camiones, brigadas y unidades municipales")
public class RecursoController {

    private final RecursoService recursoService;

    public RecursoController(RecursoService recursoService) {
        this.recursoService = recursoService;
    }

    @GetMapping("/disponibles")
    @Operation(summary = "Recursos disponibles para asignar")
    public ResponseEntity<List<RecursoResponse>> disponibles() {
        return ResponseEntity.ok(recursoService.listarDisponibles());
    }

    @GetMapping
    @Operation(summary = "Listar todos los recursos (operador/admin)")
    public ResponseEntity<List<RecursoResponse>> todos() {
        return ResponseEntity.ok(recursoService.listarTodos());
    }

    @PostMapping("/asignar-emergencia")
    @Operation(summary = "Asignar unidades a una emergencia según prioridad")
    public ResponseEntity<AsignacionRecursoResponse> asignar(@Valid @RequestBody AsignacionRecursoRequest body) {
        AsignacionRecursoResponse res = recursoService.asignarParaEmergencia(
                body.getEmergenciaId(),
                body.getPrioridad());
        return ResponseEntity.ok(res);
    }

    @PostMapping("/liberar-emergencia/{emergenciaId}")
    @Operation(summary = "Devuelve recursos al pool cuando la emergencia termina")
    public ResponseEntity<Void> liberar(@PathVariable Long emergenciaId) {
        recursoService.liberarPorEmergencia(emergenciaId);
        return ResponseEntity.noContent().build();
    }
}
