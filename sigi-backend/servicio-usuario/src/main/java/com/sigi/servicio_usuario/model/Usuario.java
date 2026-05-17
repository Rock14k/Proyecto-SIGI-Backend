package com.sigi.servicio_usuario.model;

// Importaciones de JPA para mapear esta clase a una tabla de la base de datos
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// @Entity le dice a JPA que esta clase corresponde a una tabla en la base de datos
@Entity
// @Table especifica el nombre exacto de la tabla
@Table(name = "usuarios")
// @Data de Lombok genera: getters, setters, toString, equals y hashCode
@Data
// @AllArgsConstructor genera un constructor con todos los campos
@AllArgsConstructor
// @NoArgsConstructor genera un constructor vacío (requerido por JPA)
@NoArgsConstructor
public class Usuario {

    // @Id indica que este campo es la clave primaria
    @Id
    // @GeneratedValue le dice a la BD que genere el ID automáticamente
    // IDENTITY usa el auto_increment de MySQL
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // @Column define cómo se llama la columna y sus restricciones
    @Column(name = "nombre", nullable = false, length = 100)
    // @NotBlank valida que el campo no esté vacío
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @Column(name = "apellido", nullable = false, length = 100)
    @NotBlank(message = "El apellido es obligatorio")
    private String apellido;
     
    @Column(name="rut", nullable = false, unique = true, length = 12)
    @NotBlank(message = "El RUT es obligatorio")
    private String rut;

    // unique = true significa que no puede haber dos usuarios con el mismo email
    @Column(name = "email", nullable = false, unique = true, length = 150)
    @NotBlank(message = "El email es obligatorio")
    // @Email valida que el formato sea de un email válido
    @Email(message = "El email debe tener un formato válido")
    private String email;

    @Column(name = "password", nullable = false)
    @NotBlank(message = "La contraseña es obligatoria")
    // @Size valida el tamaño mínimo y máximo
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String password;

    // @Enumerated le dice a JPA cómo guardar el enum en la base de datos
    // EnumType.STRING guarda el texto "CIUDADANO", "OPERADOR", etc.
    @Enumerated(EnumType.STRING)
    @Column(name = "rol", nullable = false, length = 30)
    private Rol rol;

    // Número de teléfono del usuario para notificaciones
    @Column(name = "telefono", length = 20)
    private String telefono;

    // Fecha y hora cuando se creó el usuario
    // @Column(updatable = false) significa que este campo no se puede cambiar después
    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime fechaCreacion;

    // Si la cuenta está activa o no
    @Column(name = "activo")
    private boolean activo = true;

    /** ID en servicio-media (foto de perfil) */
    @Column(name = "foto_media_id")
    private Long fotoMediaId;

    /** Certificado de residencia (obligatorio al registrarse como ciudadano) */
    @Column(name = "certificado_residencia_media_id")
    private Long certificadoResidenciaMediaId;

    // @PrePersist se ejecuta ANTES de guardar el usuario en la base de datos
    // Así siempre se guarda automáticamente la fecha de creación
    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
    }

    // Enum para los roles del sistema 
    // CIUDADANO: puede reportar incendios y ver sus reportes
    // OPERADOR: puede validar reportes y gestionar emergencias
    // EQUIPO_EMERGENCIA: puede recibir alertas y actualizar el estado
    public enum Rol {
        CIUDADANO,
        OPERADOR_MUNICIPAL,
        EQUIPO_EMERGENCIA,
        BRIGADISTA,
        BOMBERO,
        AMBULANCIA,
        SEGURIDAD_MUNICIPAL,
        ADMIN
    }
}