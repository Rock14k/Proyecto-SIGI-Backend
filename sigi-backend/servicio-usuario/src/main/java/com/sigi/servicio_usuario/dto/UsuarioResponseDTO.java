package com.sigi.servicio_usuario.dto;


import java.time.LocalDateTime;

import com.sigi.servicio_usuario.model.Usuario;

import lombok.Data;

// DTO para enviar datos del usuario al cliente
// NUNCA enviamos la contraseña en la respuesta
@Data
public class UsuarioResponseDTO {

    private Long id;
    private String nombre;
    private String apellido;
    private String rut;
    private String email;
    private Usuario.Rol rol;
    private String telefono;
    private LocalDateTime fechaCreacion;
    private boolean activo;
    private Long fotoMediaId;
    private String fotoUrl;

    // Constructor estático que convierte una entidad Usuario a este DTO
    // Es más limpio que hacerlo en el servicio
    public static UsuarioResponseDTO fromEntity(Usuario usuario) {
        UsuarioResponseDTO dto = new UsuarioResponseDTO();
        dto.setId(usuario.getId());
        dto.setNombre(usuario.getNombre());
        dto.setApellido(usuario.getApellido());
        dto.setRut(usuario.getRut());
        dto.setEmail(usuario.getEmail());
        dto.setRol(usuario.getRol());
        dto.setTelefono(usuario.getTelefono());
        dto.setFechaCreacion(usuario.getFechaCreacion());
        dto.setActivo(usuario.isActivo());
        dto.setFotoMediaId(usuario.getFotoMediaId());
        if (usuario.getFotoMediaId() != null) {
            dto.setFotoUrl("/api/media/" + usuario.getFotoMediaId() + "/archivo");
        }
        return dto;  // Retornamos el DTO sin la contraseña
    }
}