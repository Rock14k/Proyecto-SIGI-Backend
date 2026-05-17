package com.sigi.servicio_empleo.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sigi.servicio_empleo.dto.EmpleoRequest;
import com.sigi.servicio_empleo.dto.EmpleoResponse;
import com.sigi.servicio_empleo.dto.PostulacionResponse;
import com.sigi.servicio_empleo.service.EmpleoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/empleos")
@Tag(name = "Empleos", description = "Avisos laborales y postulaciones")
public class EmpleoController {

    private final EmpleoService empleoService;

    public EmpleoController(EmpleoService empleoService) {
        this.empleoService = empleoService;
    }

    @GetMapping
    @Operation(summary = "Listar empleos activos")
    public ResponseEntity<List<EmpleoResponse>> listar() {
        return ResponseEntity.ok(empleoService.listarActivos());
    }

    @GetMapping("/admin/todos")
    @Operation(summary = "Listar todos (admin)")
    public ResponseEntity<List<EmpleoResponse>> listarTodos(@RequestHeader("X-User-Role") String rol) {
        if (!"ADMIN".equals(rol)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(empleoService.listarTodosAdmin());
    }

    @PostMapping
    @Operation(summary = "Crear aviso (admin)")
    public ResponseEntity<EmpleoResponse> crear(
            @RequestHeader("X-User-Role") String rol,
            @Valid @RequestBody EmpleoRequest body) {
        if (!"ADMIN".equals(rol)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(empleoService.crear(body));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar aviso (admin)")
    public ResponseEntity<EmpleoResponse> actualizar(
            @RequestHeader("X-User-Role") String rol,
            @PathVariable Long id,
            @Valid @RequestBody EmpleoRequest body) {
        if (!"ADMIN".equals(rol)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        try {
            return ResponseEntity.ok(empleoService.actualizar(id, body));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Desactivar aviso (admin)")
    public ResponseEntity<Void> eliminar(
            @RequestHeader("X-User-Role") String rol,
            @PathVariable Long id) {
        if (!"ADMIN".equals(rol)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        try {
            empleoService.eliminar(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{id}/postular")
    @Operation(summary = "Postular a un empleo (ciudadano)")
    public ResponseEntity<PostulacionResponse> postular(
            @RequestHeader("X-User-Id") Long usuarioId,
            @PathVariable Long id) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(empleoService.postular(id, usuarioId));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/postulaciones/mias")
    @Operation(summary = "Mis postulaciones")
    public ResponseEntity<List<PostulacionResponse>> misPostulaciones(
            @RequestHeader("X-User-Id") Long usuarioId) {
        return ResponseEntity.ok(empleoService.misPostulaciones(usuarioId));
    }

    @GetMapping("/postulaciones")
    @Operation(summary = "Todas las postulaciones (admin y operador municipal)")
    public ResponseEntity<List<PostulacionResponse>> todas(
            @RequestHeader("X-User-Role") String rol) {
        if (!"ADMIN".equals(rol) && !"OPERADOR_MUNICIPAL".equals(rol)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(empleoService.todasPostulaciones());
    }
}
