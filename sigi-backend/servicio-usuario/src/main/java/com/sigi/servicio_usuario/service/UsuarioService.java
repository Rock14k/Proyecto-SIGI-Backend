package com.sigi.servicio_usuario.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.sigi.servicio_usuario.dto.AuthResponseDTO;
import com.sigi.servicio_usuario.dto.LoginDTO;
import com.sigi.servicio_usuario.dto.UsuarioRegistroDTO;
import com.sigi.servicio_usuario.dto.UsuarioResponseDTO;
import com.sigi.servicio_usuario.model.Usuario;
import com.sigi.servicio_usuario.repository.UsuarioRepository;
import com.sigi.servicio_usuario.security.JwtUtil;

// @Service indica que esta clase contiene la lógica de negocio
// Spring la registra como un Bean
@Service
public class UsuarioService {

    // @Autowired le dice a Spring que inyecte automáticamente la dependencia
    // Esto es la "Inyección de Dependencias" - un principio clave de Spring
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;  // Para encriptar contraseñas

    @Autowired
    private JwtUtil jwtUtil;  // Para generar tokens JWT

    // Registrar un nuevo usuario
    public UsuarioResponseDTO registrar(UsuarioRegistroDTO dto) {
        
        // Verificamos si ya existe un usuario con ese email
        if (usuarioRepository.existsByEmail(dto.getEmail())) {
            // Lanzamos una excepción si el email ya está registrado
            throw new RuntimeException("Ya existe un usuario con el email: " + dto.getEmail());
        }

        // Creamos la entidad Usuario a partir del DTO
        Usuario usuario = new Usuario();
        usuario.setNombre(dto.getNombre());
        usuario.setApellido(dto.getApellido());
        usuario.setRut(dto.getRut());
        usuario.setEmail(dto.getEmail());
        
        // IMPORTANTE: NUNCA guardamos la contraseña en texto plano
        // passwordEncoder.encode() la encripta con BCrypt
        usuario.setPassword(passwordEncoder.encode(dto.getPassword()));
        
        usuario.setRol(dto.getRol());
        usuario.setTelefono(dto.getTelefono());
        usuario.setActivo(true);

        // Guardamos el usuario en la base de datos
        // save() hace INSERT en la base de datos y retorna el usuario con su ID
        Usuario usuarioGuardado = usuarioRepository.save(usuario);

        // Convertimos la entidad a DTO para no exponer la contraseña
        return UsuarioResponseDTO.fromEntity(usuarioGuardado);
    }

    // Inicio de sesión: valida credenciales y genera token JWT
    public AuthResponseDTO login(LoginDTO dto) {
        
        // Buscamos el usuario por email
        // orElseThrow() lanza excepción si no se encuentra
        Usuario usuario = usuarioRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Verificamos que la contraseña coincida
        // passwordEncoder.matches() compara la contraseña plana con la encriptada
        if (!passwordEncoder.matches(dto.getPassword(), usuario.getPassword())) {
            throw new RuntimeException("Contraseña incorrecta");
        }

        // Verificamos que la cuenta esté activa
        if (!usuario.isActivo()) {
            throw new RuntimeException("La cuenta está desactivada");
        }

        // Generamos el token JWT con el email y rol del usuario
        String token = jwtUtil.generateToken(
            usuario.getEmail(), 
            usuario.getRol().name()  // .name() convierte el enum a String
        );

        // Retornamos la respuesta con el token
        return new AuthResponseDTO(
            token,
            "Bearer",           // Tipo de token
            usuario.getEmail(),
            usuario.getRol().name(),
            86400L              // 24 horas en segundos
        );
    }

    // Obtener todos los usuarios (solo para administradores)
    public List<UsuarioResponseDTO> obtenerTodos() {
        // findAll() obtiene todos los registros de la tabla
        // stream() convierte la lista en un Stream para procesar
        // map() transforma cada Usuario a UsuarioResponseDTO
        // collect() convierte el Stream de vuelta a una Lista
        return usuarioRepository.findAll()
                .stream()
                .map(UsuarioResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // Obtener un usuario por ID
    public UsuarioResponseDTO obtenerPorId(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));
        return UsuarioResponseDTO.fromEntity(usuario);
    }

    // Desactivar una cuenta de usuario
    public void desactivarUsuario(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));
        
        // En vez de borrar el registro, lo desactivamos
        // Esto se llama "soft delete" (eliminación suave)
        usuario.setActivo(false);
        usuarioRepository.save(usuario);
    }
}