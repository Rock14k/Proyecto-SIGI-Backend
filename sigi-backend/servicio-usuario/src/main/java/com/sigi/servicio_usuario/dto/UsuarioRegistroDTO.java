package com.sigi.servicio_usuario.dto;

// DTO = Data Transfer Object
// Es un objeto que usamos para transferir datos entre capas
// NO es la entidad de la base de datos, es solo para recibir/enviar datos

import com.sigi.servicio_usuario.model.Usuario;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

// @Data de Lombok nos ahorra escribir getters y setters manualmente
@Data
public class UsuarioRegistroDTO {

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    private String apellido;

     @NotBlank(message = "El rut es obligatorio")
    private String rut;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Formato de email inválido")
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String password;

    // El rol que tendrá el usuario en el sistema
    @NotNull(message = "El rol es obligatorio")
    private Usuario.Rol rol;

    // Teléfono opcional para notificaciones
    private String telefono;
}
