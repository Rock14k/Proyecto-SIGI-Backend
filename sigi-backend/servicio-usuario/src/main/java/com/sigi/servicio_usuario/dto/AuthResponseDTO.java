package com.sigi.servicio_usuario.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

// DTO para la respuesta de autenticación
// Contiene el token JWT y datos básicos del usuario
@Data
@AllArgsConstructor  // Lombok genera el constructor con todos los campos
public class AuthResponseDTO {

    // El token JWT que el cliente debe usar en futuras peticiones
    private String token;
    
    // Tipo de token (siempre "Bearer" en JWT)
    private String tipoToken;
    
    // Email del usuario autenticado
    private String email;
    
    // Rol del usuario (para que el frontend sepa qué mostrar)
    private String rol;
    
    // Cuántos segundos es válido el token
    private Long expiracionEnSegundos;
}