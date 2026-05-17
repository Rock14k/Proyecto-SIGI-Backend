package com.sigi.servicio_usuario.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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

import com.sigi.servicio_usuario.dto.ActualizarFotoRequest;
import com.sigi.servicio_usuario.dto.ActualizarRolRequest;
import com.sigi.servicio_usuario.dto.AdminCrearUsuarioDTO;
import com.sigi.servicio_usuario.dto.UsuarioResponseDTO;
import com.sigi.servicio_usuario.service.UsuarioService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/usuarios")
@Tag(name = "Usuarios", description = "Gestión de usuarios del sistema SIGI")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    private static boolean esAdmin(String rol) {
        return "ADMIN".equals(rol);
    }

    private static boolean esOperadorOAdmin(String rol) {
        return "ADMIN".equals(rol) || "OPERADOR_MUNICIPAL".equals(rol);
    }

    @GetMapping
    @Operation(summary = "Listar usuarios (admin)")
    public ResponseEntity<List<UsuarioResponseDTO>> listarTodos(
            @RequestHeader(value = "X-User-Role", required = false) String rol) {
        if (!esAdmin(rol)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(usuarioService.obtenerTodos());
    }

    @GetMapping("/emergencia")
    @Operation(summary = "Personal de emergencia activo (admin/operador)")
    public ResponseEntity<List<UsuarioResponseDTO>> listarEmergencia(
            @RequestHeader(value = "X-User-Role", required = false) String rol) {
        if (!esOperadorOAdmin(rol)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(usuarioService.listarPersonalEmergencia());
    }

    @PostMapping
    @Operation(summary = "Crear usuario (solo admin)")
    public ResponseEntity<UsuarioResponseDTO> crear(
            @RequestHeader(value = "X-User-Role", required = false) String rol,
            @Valid @RequestBody AdminCrearUsuarioDTO body) {
        if (!esAdmin(rol)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.crearPorAdmin(body));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar usuario por ID")
    public ResponseEntity<UsuarioResponseDTO> obtenerPorId(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(usuarioService.obtenerPorId(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/me/foto")
    @Operation(summary = "Actualizar foto de perfil (mediaId)")
    public ResponseEntity<UsuarioResponseDTO> actualizarFoto(
            @RequestHeader("X-User-Id") Long usuarioId,
            @Valid @RequestBody ActualizarFotoRequest body) {
        try {
            return ResponseEntity.ok(usuarioService.actualizarFotoPerfil(usuarioId, body.getFotoMediaId()));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/rol")
    @Operation(summary = "Cambiar rol de usuario (solo admin)")
    public ResponseEntity<UsuarioResponseDTO> actualizarRol(
            @RequestHeader(value = "X-User-Role", required = false) String rol,
            @PathVariable Long id,
            @Valid @RequestBody ActualizarRolRequest body) {
        if (!esAdmin(rol)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        try {
            return ResponseEntity.ok(usuarioService.actualizarRol(id, body));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/suspender")
    @Operation(summary = "Suspender usuario (solo admin)")
    public ResponseEntity<Void> suspender(
            @RequestHeader(value = "X-User-Role", required = false) String rol,
            @PathVariable Long id) {
        if (!esAdmin(rol)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        try {
            usuarioService.desactivarUsuario(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/reactivar")
    @Operation(summary = "Reactivar usuario suspendido (solo admin)")
    public ResponseEntity<UsuarioResponseDTO> reactivar(
            @RequestHeader(value = "X-User-Role", required = false) String rol,
            @PathVariable Long id) {
        if (!esAdmin(rol)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        try {
            return ResponseEntity.ok(usuarioService.reactivarUsuario(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar usuario permanentemente (solo admin)")
    public ResponseEntity<Void> eliminar(
            @RequestHeader(value = "X-User-Role", required = false) String rol,
            @PathVariable Long id) {
        if (!esAdmin(rol)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        try {
            usuarioService.eliminarUsuario(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
