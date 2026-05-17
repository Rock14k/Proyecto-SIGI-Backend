package com.sigi.servicio_usuario.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sigi.servicio_usuario.dto.AdminCrearUsuarioDTO;
import com.sigi.servicio_usuario.dto.AuthResponseDTO;
import com.sigi.servicio_usuario.dto.LoginDTO;
import com.sigi.servicio_usuario.dto.ActualizarRolRequest;
import com.sigi.servicio_usuario.dto.UsuarioRegistroDTO;
import com.sigi.servicio_usuario.dto.UsuarioResponseDTO;
import com.sigi.servicio_usuario.model.Usuario;
import com.sigi.servicio_usuario.repository.UsuarioRepository;
import com.sigi.servicio_usuario.security.JwtUtil;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    public UsuarioResponseDTO registrar(UsuarioRegistroDTO dto) {
        if (usuarioRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Ya existe un usuario con el email: " + dto.getEmail());
        }
        if (usuarioRepository.existsByRut(dto.getRut())) {
            throw new RuntimeException("Ya existe un usuario con el RUT: " + dto.getRut());
        }

        Usuario usuario = new Usuario();
        usuario.setNombre(dto.getNombre());
        usuario.setApellido(dto.getApellido());
        usuario.setRut(dto.getRut());
        usuario.setEmail(dto.getEmail());
        usuario.setPassword(passwordEncoder.encode(dto.getPassword()));
        usuario.setRol(Usuario.Rol.CIUDADANO);
        usuario.setTelefono(dto.getTelefono());
        usuario.setCertificadoResidenciaMediaId(dto.getCertificadoResidenciaMediaId());
        usuario.setActivo(true);

        return UsuarioResponseDTO.fromEntity(usuarioRepository.save(usuario));
    }

    @Transactional
    public UsuarioResponseDTO crearPorAdmin(AdminCrearUsuarioDTO dto) {
        if (usuarioRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Ya existe un usuario con el email: " + dto.getEmail());
        }
        if (usuarioRepository.existsByRut(dto.getRut())) {
            throw new RuntimeException("Ya existe un usuario con el RUT: " + dto.getRut());
        }

        Usuario usuario = new Usuario();
        usuario.setNombre(dto.getNombre());
        usuario.setApellido(dto.getApellido());
        usuario.setRut(dto.getRut());
        usuario.setEmail(dto.getEmail());
        usuario.setPassword(passwordEncoder.encode(dto.getPassword()));
        usuario.setRol(dto.getRol());
        usuario.setTelefono(dto.getTelefono());
        usuario.setCertificadoResidenciaMediaId(dto.getCertificadoResidenciaMediaId());
        usuario.setActivo(true);

        return UsuarioResponseDTO.fromEntity(usuarioRepository.save(usuario));
    }

    public AuthResponseDTO login(LoginDTO dto) {
        Usuario usuario = usuarioRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (!passwordEncoder.matches(dto.getPassword(), usuario.getPassword())) {
            throw new RuntimeException("Contraseña incorrecta");
        }
        if (!usuario.isActivo()) {
            throw new RuntimeException("La cuenta está desactivada");
        }

        String token = jwtUtil.generateToken(
                usuario.getEmail(),
                usuario.getRol().name(),
                usuario.getId(),
                usuario.getNombre(),
                usuario.getApellido());

        return new AuthResponseDTO(
                token,
                "Bearer",
                usuario.getEmail(),
                usuario.getRol().name(),
                usuario.getId(),
                86400L,
                usuario.getNombre(),
                usuario.getApellido());
    }

    public List<UsuarioResponseDTO> obtenerTodos() {
        return usuarioRepository.findAll().stream()
                .map(UsuarioResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public List<UsuarioResponseDTO> listarPorRol(Usuario.Rol rol) {
        return usuarioRepository.findByRol(rol).stream()
                .map(UsuarioResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public List<UsuarioResponseDTO> listarPersonalEmergencia() {
        return usuarioRepository.findAll().stream()
                .filter(u -> u.isActivo() && esRolEmergencia(u.getRol()))
                .map(UsuarioResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public UsuarioResponseDTO obtenerPorId(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));
        return UsuarioResponseDTO.fromEntity(usuario);
    }

    @Transactional
    public UsuarioResponseDTO actualizarFotoPerfil(Long usuarioId, Long fotoMediaId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        usuario.setFotoMediaId(fotoMediaId);
        return UsuarioResponseDTO.fromEntity(usuarioRepository.save(usuario));
    }

    @Transactional
    public UsuarioResponseDTO actualizarRol(Long id, ActualizarRolRequest req) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        usuario.setRol(req.getRol());
        return UsuarioResponseDTO.fromEntity(usuarioRepository.save(usuario));
    }

    public void desactivarUsuario(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));
        usuario.setActivo(false);
        usuarioRepository.save(usuario);
    }

    @Transactional
    public UsuarioResponseDTO reactivarUsuario(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));
        usuario.setActivo(true);
        return UsuarioResponseDTO.fromEntity(usuarioRepository.save(usuario));
    }

    @Transactional
    public void eliminarUsuario(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new RuntimeException("Usuario no encontrado con ID: " + id);
        }
        usuarioRepository.deleteById(id);
    }

    private static boolean esRolEmergencia(Usuario.Rol rol) {
        return rol == Usuario.Rol.EQUIPO_EMERGENCIA
                || rol == Usuario.Rol.BRIGADISTA
                || rol == Usuario.Rol.BOMBERO
                || rol == Usuario.Rol.AMBULANCIA
                || rol == Usuario.Rol.SEGURIDAD_MUNICIPAL;
    }
}
