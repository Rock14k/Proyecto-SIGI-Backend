package com.sigi.servicio_usuario.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.sigi.servicio_usuario.model.Usuario;
import com.sigi.servicio_usuario.repository.UsuarioRepository;

@Configuration
public class UsuariosPruebaInitializer {

    @Bean
    @Order(1)
    CommandLineRunner cargarUsuariosDemo(UsuarioRepository repo, PasswordEncoder encoder) {
        return args -> {
            crearSiNoExiste(repo, encoder, "Hawk", "Durant", "12.345.678-9",
                    "hawk.durant@test.com", "secreta123", Usuario.Rol.CIUDADANO, "+56911112222");
            crearSiNoExiste(repo, encoder, "Emilio", "Jaramillo", "11.222.333-4",
                    "emilio.jaramillo@municipalidad.cl", "operador123", Usuario.Rol.OPERADOR_MUNICIPAL, "+56933334444");
            crearSiNoExiste(repo, encoder, "Rodrigo", "Candia", "18.765.432-1",
                    "rodrigo.candia@municipalidad.cl", "admin123", Usuario.Rol.ADMIN, "+56944445555");
            crearSiNoExiste(repo, encoder, "Carla", "Méndez", "14.555.666-7",
                    "brigada@municipalidad.cl", "brigada123", Usuario.Rol.BRIGADISTA, "+56955556666");
            crearSiNoExiste(repo, encoder, "Luis", "Fuentes", "15.666.777-8",
                    "bomberos@municipalidad.cl", "bomberos123", Usuario.Rol.BOMBERO, "+56966667777");
            crearSiNoExiste(repo, encoder, "Ana", "Rojas", "16.777.888-9",
                    "ambulancia@municipalidad.cl", "ambulancia123", Usuario.Rol.AMBULANCIA, "+56977778888");
            crearSiNoExiste(repo, encoder, "Pedro", "Silva", "17.888.999-0",
                    "seguridad@municipalidad.cl", "seguridad123", Usuario.Rol.SEGURIDAD_MUNICIPAL, "+56988889999");
        };
    }

    private static void crearSiNoExiste(
            UsuarioRepository repo,
            PasswordEncoder encoder,
            String nombre,
            String apellido,
            String rut,
            String email,
            String password,
            Usuario.Rol rol,
            String telefono) {
        if (repo.existsByEmail(email)) {
            return;
        }
        Usuario u = new Usuario();
        u.setNombre(nombre);
        u.setApellido(apellido);
        u.setRut(rut);
        u.setEmail(email);
        u.setPassword(encoder.encode(password));
        u.setRol(rol);
        u.setTelefono(telefono);
        u.setActivo(true);
        repo.save(u);
    }
}
