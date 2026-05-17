package com.sigi.servicio_reporte.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sigi.servicio_reporte.client.EmergenciaClient;
import com.sigi.servicio_reporte.client.UsuarioClient;
import com.sigi.servicio_reporte.client.dto.CoordenadasDto;
import com.sigi.servicio_reporte.dto.CrearReporteRequest;
import com.sigi.servicio_reporte.dto.ReporteResponse;
import com.sigi.servicio_reporte.model.Reporte;
import com.sigi.servicio_reporte.model.Reporte.EstadoReporte;
import com.sigi.servicio_reporte.model.Reporte.PrioridadReporte;
import com.sigi.servicio_reporte.repository.ReporteRepository;
import com.sigi.servicio_reporte.service.PrioridadMetricasService.ResultadoPrioridad;

@ExtendWith(MockitoExtension.class)
class ReporteServiceTest {

    @Mock
    private ReporteRepository reporteRepository;

    @Mock
    private UbicacionConsultaService ubicacionConsultaService;

    @Mock
    private EmergenciaClient emergenciaClient;

    @Mock
    private PrioridadMetricasService prioridadMetricasService;

    @Mock
    private UsuarioClient usuarioClient;

    @InjectMocks
    private ReporteService reporteService;

    @BeforeEach
    void configurarPrioridadMetricas() {
        when(prioridadMetricasService.calcular(anyString(), anyString()))
                .thenReturn(new ResultadoPrioridad(PrioridadReporte.MEDIA, 0, "Prioridad por métricas"));
    }

    @Test
    @DisplayName("Crea reporte con coordenadas cuando ubicación responde")
    void conCoordenadas() {
        CrearReporteRequest req = new CrearReporteRequest();
        req.setDescripcion("Humo negro");
        req.setDireccion("Larraín 123");
        req.setPrioridad("ALTA");

        CoordenadasDto ok = new CoordenadasDto();
        ok.setLatitud(-33.1);
        ok.setLongitud(-70.2);
        ok.setMensaje("ok");
        when(ubicacionConsultaService.obtenerCoordenadas("Larraín 123")).thenReturn(ok);

        when(reporteRepository.save(any(Reporte.class))).thenAnswer(inv -> {
            Reporte r = inv.getArgument(0);
            r.setId(9L);
            return r;
        });

        ReporteResponse res = reporteService.crearReporte(1L, "CIUDADANO", req);

        assertEquals(-33.1, res.getLatitud());
        assertEquals(-70.2, res.getLongitud());
    }

    @Test
    @DisplayName("Crea reporte sin coordenadas si el circuito devuelve fallback")
    void sinCoordenadas() {
        CrearReporteRequest req = new CrearReporteRequest();
        req.setDescripcion("Fuego pastizal");
        req.setDireccion("Camino rural K");
        req.setPrioridad("BAJA");

        CoordenadasDto vacio = new CoordenadasDto();
        vacio.setLatitud(null);
        vacio.setLongitud(null);
        vacio.setMensaje("No disponible");
        when(ubicacionConsultaService.obtenerCoordenadas("Camino rural K")).thenReturn(vacio);

        when(reporteRepository.save(any(Reporte.class))).thenAnswer(inv -> {
            Reporte r = inv.getArgument(0);
            r.setId(2L);
            return r;
        });

        ReporteResponse res = reporteService.crearReporte(5L, "CIUDADANO", req);

        assertNull(res.getLatitud());
        assertNull(res.getLongitud());
    }

    @Test
    @DisplayName("Validar aprobado llama a servicio emergencias")
    void validarDisparaEmergencia() {
        Reporte r = new Reporte();
        r.setId(10L);
        r.setUsuarioId(3L);
        r.setDescripcion("test");
        r.setDireccion("curicó");
        r.setEstado(EstadoReporte.PENDIENTE);
        r.setPrioridad(PrioridadReporte.CRITICA);

        when(reporteRepository.findById(10L)).thenReturn(Optional.of(r));
        when(reporteRepository.save(any(Reporte.class))).thenAnswer(i -> i.getArgument(0));

        com.sigi.servicio_reporte.dto.ValidarReporteRequest vr =
                new com.sigi.servicio_reporte.dto.ValidarReporteRequest();
        vr.setAprobado(true);
        vr.setNotasOperador("Confirmado por operador Hawk Durant");

        reporteService.validar(10L, 99L, vr);

        verify(emergenciaClient).crearDesdeReporte(any());
    }
}
