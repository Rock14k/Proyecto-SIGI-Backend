package com.sigi.servicio_usuario.controller;


import com.sigi.servicio_usuario.dto.UsuarioResponseDTO;
import com.sigi.servicio_usuario.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@Tag(name = "Usuarios", description = "Gestión de usuarios del sistema SIGI")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    // Obtener todos los usuarios
    // @GetMapping maneja peticiones GET
    @GetMapping
    @Operation(summary = "Listar usuarios", description = "Obtiene todos los usuarios del sistema")
    public ResponseEntity<List<UsuarioResponseDTO>> listarTodos() {
        return ResponseEntity.ok(usuarioService.obtenerTodos());
    }

    // Obtener usuario por ID
    // {id} es una variable en la URL
    @GetMapping("/{id}")
    @Operation(summary = "Buscar usuario por ID")
    public ResponseEntity<UsuarioResponseDTO> obtenerPorId(
            // @PathVariable extrae el valor de {id} de la URL
            @PathVariable Long id) {
        
        try {
            return ResponseEntity.ok(usuarioService.obtenerPorId(id));
        } catch (RuntimeException e) {
            // 404 = Not Found (no encontrado)
            return ResponseEntity.notFound().build();
        }
    }

    // Desactivar usuario (soft delete)
    // @DeleteMapping maneja peticiones DELETE
    @DeleteMapping("/{id}")
    @Operation(summary = "Desactivar usuario", description = "Desactiva la cuenta del usuario sin borrarla")
    public ResponseEntity<Void> desactivar(@PathVariable Long id) {
        try {
            usuarioService.desactivarUsuario(id);
            // 204 = No Content (operación exitosa sin datos que retornar)
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}