package com.sigi.servicio_reporte.controller;

import java.util.List;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sigi.servicio_reporte.dto.ActualizarPrioridadRequest;
import com.sigi.servicio_reporte.dto.AsignarReporteRequest;
import com.sigi.servicio_reporte.dto.ClasificarReporteRequest;
import com.sigi.servicio_reporte.dto.CrearReporteRequest;
import com.sigi.servicio_reporte.dto.ReporteResponse;
import com.sigi.servicio_reporte.dto.ValidarReporteRequest;
import com.sigi.servicio_reporte.service.ReporteService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/reportes")
@Tag(name = "Reportes", description = "Reportes ciudadanos de incendios")
public class ReporteController {

    private static final Set<String> ROLES_OPERADOR = Set.of("ADMIN", "OPERADOR_MUNICIPAL");
    private static final Set<String> ROLES_EQUIPO = Set.of(
            "EQUIPO_EMERGENCIA",
            "BRIGADISTA",
            "BOMBERO",
            "AMBULANCIA",
            "SEGURIDAD_MUNICIPAL",
            "ADMIN",
            "OPERADOR_MUNICIPAL");

    private final ReporteService reporteService;

    public ReporteController(ReporteService reporteService) {
        this.reporteService = reporteService;
    }

    @GetMapping
    @Operation(summary = "Listar todos los reportes (operador/admin)")
    public ResponseEntity<List<ReporteResponse>> listarTodos(@RequestHeader("X-User-Role") String rol) {
        if (!ROLES_OPERADOR.contains(rol)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(reporteService.listarTodos());
    }

    @GetMapping("/equipo")
    @Operation(summary = "Listar reportes para brigadas, bomberos, ambulancias y seguridad")
    public ResponseEntity<List<ReporteResponse>> listarEquipo(@RequestHeader("X-User-Role") String rol) {
        if (!ROLES_EQUIPO.contains(rol)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(reporteService.listarParaEquipo());
    }

    @PostMapping
    @Operation(summary = "Crear reporte (ciudadano, operador o equipo)")
    public ResponseEntity<ReporteResponse> crear(
            @RequestHeader("X-User-Id") Long usuarioId,
            @RequestHeader("X-User-Role") String rol,
            @Valid @RequestBody CrearReporteRequest body) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(reporteService.crearReporte(usuarioId, rol, body));
    }

    @GetMapping("/usuario/{usuarioId}")
    @Operation(summary = "Reportes de un usuario")
    public ResponseEntity<List<ReporteResponse>> porUsuario(
            @RequestHeader("X-User-Id") Long solicitanteId,
            @RequestHeader("X-User-Role") String rol,
            @PathVariable Long usuarioId) {
        try {
            return ResponseEntity.ok(reporteService.listarMisReportes(solicitanteId, usuarioId, rol));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @GetMapping("/pendientes")
    @Operation(summary = "Cola de validación (operador)")
    public ResponseEntity<List<ReporteResponse>> pendientes(@RequestHeader("X-User-Role") String rol) {
        if (!ROLES_OPERADOR.contains(rol)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(reporteService.listarPendientes());
    }

    @PutMapping("/{id}/validar")
    @Operation(summary = "Validar o rechazar reporte (operador)")
    public ResponseEntity<ReporteResponse> validar(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long operadorId,
            @RequestHeader("X-User-Role") String rol,
            @RequestBody ValidarReporteRequest body) {
        if (!ROLES_OPERADOR.contains(rol)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        try {
            return ResponseEntity.ok(reporteService.validar(id, operadorId, body));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}/prioridad")
    @Operation(summary = "Asignar prioridad manual (operador municipal)")
    public ResponseEntity<ReporteResponse> actualizarPrioridad(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long operadorId,
            @RequestHeader("X-User-Role") String rol,
            @Valid @RequestBody ActualizarPrioridadRequest body) {
        if (!ROLES_OPERADOR.contains(rol)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        try {
            return ResponseEntity.ok(reporteService.actualizarPrioridad(id, operadorId, body));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}/clasificar")
    @Operation(summary = "Clasificar reporte (equipos de emergencia)")
    public ResponseEntity<ReporteResponse> clasificar(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long equipoId,
            @RequestHeader("X-User-Role") String rol,
            @Valid @RequestBody ClasificarReporteRequest body) {
        if (!ROLES_EQUIPO.contains(rol)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        try {
            return ResponseEntity.ok(reporteService.clasificar(id, equipoId, body));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}/asignar")
    @Operation(summary = "Derivar reporte a personal de emergencia (admin/operador)")
    public ResponseEntity<ReporteResponse> asignar(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long operadorId,
            @RequestHeader("X-User-Role") String rol,
            @Valid @RequestBody AsignarReporteRequest body) {
        if (!ROLES_OPERADOR.contains(rol)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        try {
            return ResponseEntity.ok(reporteService.asignarAEquipo(id, operadorId, body));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
