package com.sigi.servicio_usuario.controller;

import com.sigi.servicio_usuario.dto.AuthResponseDTO;
import com.sigi.servicio_usuario.dto.LoginDTO;
import com.sigi.servicio_usuario.dto.UsuarioRegistroDTO;
import com.sigi.servicio_usuario.dto.UsuarioResponseDTO;
import com.sigi.servicio_usuario.service.UsuarioService;

// Importaciones de Swagger para documentar la API
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// @RestController combina @Controller y @ResponseBody
// Significa que este controlador maneja peticiones HTTP y retorna JSON
@RestController
// @RequestMapping define la URL base de todos los endpoints de este controlador
@RequestMapping("/auth")
// @Tag es de Swagger, documenta este controlador en la UI de Swagger
@Tag(name = "Autenticación", description = "Endpoints de registro e inicio de sesión")
public class AuthController {

    @Autowired
    private UsuarioService usuarioService;

    // @PostMapping maneja peticiones POST a /auth/registro
    @PostMapping("/registro")
    // @Operation documenta este endpoint en Swagger
    @Operation(summary = "Registrar nuevo usuario", 
               description = "Crea una nueva cuenta de usuario en el sistema SIGI")
    // ResponseEntity<> nos permite controlar el código de estado HTTP de la respuesta
    public ResponseEntity<UsuarioResponseDTO> registrar(
            // @Valid activa las validaciones del DTO (las anotaciones @NotBlank, @Email, etc.)
            // @RequestBody indica que los datos vienen en el cuerpo de la petición en formato JSON
            @Valid @RequestBody UsuarioRegistroDTO dto) {
        
        try {
            UsuarioResponseDTO usuario = usuarioService.registrar(dto);
            // HttpStatus.CREATED = código 201 (el recurso fue creado exitosamente)
            return ResponseEntity.status(HttpStatus.CREATED).body(usuario);
        } catch (RuntimeException e) {
            // Si hay error (email duplicado), retornamos 400 (Bad Request)
            return ResponseEntity.badRequest().build();
        }
    }

    // Endpoint de inicio de sesión
    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión", 
               description = "Autentica al usuario y retorna un token JWT")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody LoginDTO dto) {
        
        try {
            AuthResponseDTO respuesta = usuarioService.login(dto);
            // HttpStatus.OK = código 200 (todo salió bien)
            return ResponseEntity.ok(respuesta);
        } catch (RuntimeException e) {
            // Si las credenciales son incorrectas, retornamos 401 (No Autorizado)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}