package com.sigi.servicio_recurso.config;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.sigi.servicio_recurso.model.Recurso;
import com.sigi.servicio_recurso.model.Recurso.EstadoRecurso;
import com.sigi.servicio_recurso.model.Recurso.TipoRecurso;
import com.sigi.servicio_recurso.repository.RecursoRepository;

/**
 * Semilla mínima para poder probar asignaciones. Integrantes usados en descripciones (proyecto Duoc).
 */
@Component
public class DatosEjemploValleDelSol implements ApplicationRunner {

    private final RecursoRepository recursoRepository;

    public DatosEjemploValleDelSol(RecursoRepository recursoRepository) {
        this.recursoRepository = recursoRepository;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (recursoRepository.count() > 0) {
            return;
        }

        recursoRepository.save(camion("Cisterna Alpha", TipoRecurso.CAMION_CISTERNA, "CA-001", "Rodrigo Candia — revisión semanal OK"));
        recursoRepository.save(camion("Cisterna Beta", TipoRecurso.CAMION_CISTERNA, "CA-002", "Hawk Durant — dotación completa"));
        recursoRepository.save(camion("Escala Gamma", TipoRecurso.CAMION_ESCALA, "ES-101", "Emilio Jaramillo — escalera operativa"));
        recursoRepository.save(camion("Escala Delta", TipoRecurso.CAMION_ESCALA, "ES-102", "Municipalidad Valle del Sol"));

        recursoRepository.save(brigada("Brigada Cerro", "BR-SUR-01"));
        recursoRepository.save(brigada("Brigada Centro", "BR-CEN-02"));
        recursoRepository.save(brigada("Brigada Norte", "BR-NOR-03"));

        recursoRepository.save(ambulancia());
    }

    private static Recurso camion(String nombre, TipoRecurso tipo, String id, String desc) {
        Recurso r = new Recurso();
        r.setNombre(nombre);
        r.setTipo(tipo);
        r.setEstado(EstadoRecurso.DISPONIBLE);
        r.setIdentificador(id);
        r.setDescripcion(desc);
        r.setCapacidad(8000);
        return r;
    }

    private static Recurso brigada(String nombre, String id) {
        Recurso r = new Recurso();
        r.setNombre(nombre);
        r.setTipo(TipoRecurso.BRIGADA_TERRESTRES);
        r.setEstado(EstadoRecurso.DISPONIBLE);
        r.setIdentificador(id);
        r.setDescripcion("Brigada forestal Valle del Sol");
        r.setCapacidad(12);
        return r;
    }

    private static Recurso ambulancia() {
        Recurso r = new Recurso();
        r.setNombre("Ambulancia Municipal 4");
        r.setTipo(TipoRecurso.AMBULANCIA);
        r.setEstado(EstadoRecurso.DISPONIBLE);
        r.setIdentificador("AMB-04");
        r.setDescripcion("Soporte médico en incendios estructurales");
        r.setCapacidad(2);
        return r;
    }
}
