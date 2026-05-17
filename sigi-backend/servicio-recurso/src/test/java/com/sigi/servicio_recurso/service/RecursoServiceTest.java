package com.sigi.servicio_recurso.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sigi.servicio_recurso.dto.AsignacionRecursoResponse;
import com.sigi.servicio_recurso.model.Recurso;
import com.sigi.servicio_recurso.model.Recurso.EstadoRecurso;
import com.sigi.servicio_recurso.model.Recurso.TipoRecurso;
import com.sigi.servicio_recurso.repository.RecursoRepository;

@ExtendWith(MockitoExtension.class)
class RecursoServiceTest {

    @Mock
    private RecursoRepository recursoRepository;

    @InjectMocks
    private RecursoService recursoService;

    @Test
    @DisplayName("Prioridad CRITICA requiere 3 camiones y 2 brigadas")
    void necesidadCritica() {
        var n = RecursoService.calcularNecesidad("CRITICA");
        assertEquals(3, n.camiones());
        assertEquals(2, n.brigadas());
    }

    @Test
    @DisplayName("Asignación llama a guardar cada recurso tomado")
    void asignaYpersiste() {
        List<Recurso> camiones = List.of(
                c(1L, TipoRecurso.CAMION_CISTERNA),
                c(2L, TipoRecurso.CAMION_ESCALA),
                c(3L, TipoRecurso.CAMION_CISTERNA)
        );
        List<Recurso> brigadas = List.of(
                b(4L),
                b(5L)
        );

        when(recursoRepository.findByEstadoAndTipoInOrderById(
                EstadoRecurso.DISPONIBLE,
                java.util.Set.of(TipoRecurso.CAMION_CISTERNA, TipoRecurso.CAMION_ESCALA)))
                .thenReturn(camiones);
        when(recursoRepository.findByEstadoAndTipoInOrderById(
                EstadoRecurso.DISPONIBLE,
                java.util.Set.of(TipoRecurso.BRIGADA_TERRESTRES)))
                .thenReturn(brigadas);

        AsignacionRecursoResponse res = recursoService.asignarParaEmergencia(99L, "CRITICA");

        assertEquals(5, res.getRecursosAsignadosIds().size());
        verify(recursoRepository, atLeastOnce()).save(any(Recurso.class));
    }

    private static Recurso c(long id, TipoRecurso t) {
        Recurso r = new Recurso();
        r.setId(id);
        r.setTipo(t);
        r.setEstado(EstadoRecurso.DISPONIBLE);
        return r;
    }

    private static Recurso b(long id) {
        Recurso r = new Recurso();
        r.setId(id);
        r.setTipo(TipoRecurso.BRIGADA_TERRESTRES);
        r.setEstado(EstadoRecurso.DISPONIBLE);
        return r;
    }
}
