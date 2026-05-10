package com.sigi.servicio_usuario.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sigi.servicio_usuario.model.Usuario;

// Extendemos JpaRepository<TipoEntidad, TipoDelId>
// Spring Data JPA implementa automáticamente esta interfaz
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // Spring Data JPA genera el SQL automáticamente basándose en el nombre del método
    // findByEmail → SELECT * FROM usuarios WHERE email = ?
    // Optional<> porque puede que no exista un usuario con ese email
    Optional<Usuario> findByEmail(String email);

    // findByRol → SELECT * FROM usuarios WHERE rol = ?
    List<Usuario> findByRol(Usuario.Rol rol);

    // Verifica si ya existe un usuario con ese email (útil para registro)
    // existsBy... → SELECT COUNT(*) > 0 FROM usuarios WHERE email = ?
    boolean existsByEmail(String email);

    // Busca usuarios activos
    List<Usuario> findByActivoTrue();
}