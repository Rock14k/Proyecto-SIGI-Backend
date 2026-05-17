package com.sigi.servicio_recurso.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sigi.servicio_recurso.dto.AsignacionRecursoResponse;
import com.sigi.servicio_recurso.dto.RecursoResponse;
import com.sigi.servicio_recurso.model.Recurso;
import com.sigi.servicio_recurso.model.Recurso.EstadoRecurso;
import com.sigi.servicio_recurso.model.Recurso.TipoRecurso;
import com.sigi.servicio_recurso.repository.RecursoRepository;

/**
 * Reglas del informe:
 * CRITICA → 3 camiones + 2 brigadas; ALTA → 2 + 1; MEDIA → 1 + 1; BAJA → 1 camión.
 * "Camión" = cualquier unidad CAMION_CISTERNA o CAMION_ESCALA disponible.
 */
@Service
public class RecursoService {

    private static final Set<TipoRecurso> TIPOS_CAMION = Set.of(
            TipoRecurso.CAMION_CISTERNA,
            TipoRecurso.CAMION_ESCALA
    );

    private final RecursoRepository recursoRepository;

    public RecursoService(RecursoRepository recursoRepository) {
        this.recursoRepository = recursoRepository;
    }

    public List<RecursoResponse> listarDisponibles() {
        return recursoRepository.findByEstadoOrderById(EstadoRecurso.DISPONIBLE).stream()
                .map(RecursoResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public List<RecursoResponse> listarTodos() {
        return recursoRepository.findAll().stream()
                .map(RecursoResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Calcula necesidad de brigadas y camiones según la prioridad (texto como en el resto del sistema).
     */
    static Necesidad calcularNecesidad(String prioridad) {
        if (prioridad == null) {
            return new Necesidad(1, 0);
        }
        return switch (prioridad.toUpperCase()) {
            case "CRITICA" -> new Necesidad(3, 2);
            case "ALTA" -> new Necesidad(2, 1);
            case "MEDIA" -> new Necesidad(1, 1);
            default -> new Necesidad(1, 0); // BAJA u otro
        };
    }

    record Necesidad(int camiones, int brigadas) {}

    @Transactional
    public AsignacionRecursoResponse asignarParaEmergencia(Long emergenciaId, String prioridad) {
        Necesidad nec = calcularNecesidad(prioridad);

        List<Recurso> camionesTomar = tomarDisponiblesPorTipo(nec.camiones(), TIPOS_CAMION);
        List<Recurso> brigadasTomar = tomarDisponiblesPorTipo(
                nec.brigadas(),
                Set.of(TipoRecurso.BRIGADA_TERRESTRES));

        List<Long> ids = new ArrayList<>();
        StringBuilder mensaje = new StringBuilder();

        for (Recurso r : camionesTomar) {
            asignarUno(r, emergenciaId);
            ids.add(r.getId());
        }
        for (Recurso r : brigadasTomar) {
            asignarUno(r, emergenciaId);
            ids.add(r.getId());
        }

        if (ids.size() < nec.camiones() + nec.brigadas()) {
            mensaje.append("Asignación parcial: no había suficientes unidades libres. ");
        } else {
            mensaje.append("Recursos asignados según prioridad ").append(prioridad).append(". ");
        }
        return new AsignacionRecursoResponse(ids, mensaje.toString().trim());
    }

    private List<Recurso> tomarDisponiblesPorTipo(int cantidad, Set<TipoRecurso> tipos) {
        if (cantidad <= 0) {
            return List.of();
        }
        List<Recurso> lista = recursoRepository.findByEstadoAndTipoInOrderById(EstadoRecurso.DISPONIBLE, tipos);
        return lista.stream().limit(cantidad).toList();
    }

    private void asignarUno(Recurso r, Long emergenciaId) {
        r.setEstado(EstadoRecurso.ASIGNADO);
        r.setEmergenciaId(emergenciaId);
        recursoRepository.save(r);
    }

    @Transactional
    public void liberarPorEmergencia(Long emergenciaId) {
        List<Recurso> asignados = recursoRepository.findByEmergenciaId(emergenciaId);
        for (Recurso r : asignados) {
            r.setEmergenciaId(null);
            r.setEstado(EstadoRecurso.DISPONIBLE);
            recursoRepository.save(r);
        }
    }
}
