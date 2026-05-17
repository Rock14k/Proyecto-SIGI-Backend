package com.sigi.servicio_empleo.config;

import java.time.LocalDate;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.sigi.servicio_empleo.model.Empleo;
import com.sigi.servicio_empleo.repository.EmpleoRepository;

@Configuration
public class DatosEjemploEmpleos {

    @Bean
    CommandLineRunner seed(EmpleoRepository repo) {
        return args -> {
            if (repo.count() > 0) {
                return;
            }
            repo.save(empleo("Brigadista forestal", "Prevención", 2,
                    "Patrullaje y apoyo en incendios — equipo Hawk Durant / Valle del Sol",
                    LocalDate.of(2026, 6, 30)));
            repo.save(empleo("Operador central SIGI", "Emergencias", 1,
                    "Validación de reportes ciudadanos — referencia Emilio Jaramillo",
                    LocalDate.of(2026, 5, 31)));
            repo.save(empleo("Administrador municipal TI", "Informática", 1,
                    "Gestión de plataforma SIGI — referencia Rodrigo Candia",
                    LocalDate.of(2026, 8, 15)));
        };
    }

    private static Empleo empleo(String titulo, String depto, int plazas, String desc, LocalDate cierre) {
        Empleo e = new Empleo();
        e.setTitulo(titulo);
        e.setDepartamento(depto);
        e.setPlazas(plazas);
        e.setDescripcion(desc);
        e.setFechaCierre(cierre);
        e.setActivo(true);
        return e;
    }
}
