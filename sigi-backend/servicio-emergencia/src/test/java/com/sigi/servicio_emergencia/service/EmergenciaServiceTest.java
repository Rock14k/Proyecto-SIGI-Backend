package com.sigi.servicio_emergencia.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sigi.servicio_emergencia.client.NotificacionFeignClient;
import com.sigi.servicio_emergencia.client.RecursoFeignClient;
import com.sigi.servicio_emergencia.dto.ActualizarEstadoRequest;
import com.sigi.servicio_emergencia.dto.CrearEmergenciaRequest;
import com.sigi.servicio_emergencia.dto.EmergenciaResponse;
import com.sigi.servicio_emergencia.model.Emergencia;
import com.sigi.servicio_emergencia.model.Emergencia.EstadoEmergencia;
import com.sigi.servicio_emergencia.model.Emergencia.PrioridadIncendio;
import com.sigi.servicio_emergencia.repository.EmergenciaRepository;

@ExtendWith(MockitoExtension.class)
class EmergenciaServiceTest {

    @Mock
    private EmergenciaRepository emergenciaRepository;

    @Mock
    private RecursoFeignClient recursoFeignClient;

    @Mock
    private NotificacionFeignClient notificacionFeignClient;

    @InjectMocks
    private EmergenciaService emergenciaService;

    @Test
    @DisplayName("Crear emergencia ACTIVA y dispara Feign a recursos y notificaciones")
    void crearDesdeReporte() {
        when(emergenciaRepository.save(any(Emergencia.class))).thenAnswer(inv -> {
            Emergencia e = inv.getArgument(0);
            e.setId(40L);
            return e;
        });

        CrearEmergenciaRequest req = new CrearEmergenciaRequest();
        req.setReporteId(9L);
        req.setUsuarioReportanteId(1L);
        req.setDescripcion("Humo en sector norte");
        req.setDireccion("Calle Los Pinos 200");
        req.setPrioridad("ALTA");

        EmergenciaResponse res = emergenciaService.crearDesdeReporteValidado(req);

        assertEquals(40L, res.getId());
        verify(recursoFeignClient).asignar(any());
        verify(notificacionFeignClient).crearAlerta(any());
    }

    @Test
    @DisplayName("Al pasar a RESUELTA se libera recursos")
    void resolverLibera() {
        Emergencia e = new Emergencia();
        e.setId(5L);
        e.setEstado(EstadoEmergencia.EN_PROCESO);
        e.setPrioridad(PrioridadIncendio.MEDIA);
        when(emergenciaRepository.findById(5L)).thenReturn(Optional.of(e));
        when(emergenciaRepository.save(any(Emergencia.class))).thenAnswer(inv -> inv.getArgument(0));

        ActualizarEstadoRequest act = new ActualizarEstadoRequest();
        act.setEstado("RESUELTA");
        act.setNotas("Incendio liquidado por brigada");

        EmergenciaResponse out = emergenciaService.actualizarEstado(5L, act);

        assertEquals("RESUELTA", out.getEstado());
        assertNotNull(out.getFechaResolucion());
        verify(recursoFeignClient).liberar(5L);
    }
}
