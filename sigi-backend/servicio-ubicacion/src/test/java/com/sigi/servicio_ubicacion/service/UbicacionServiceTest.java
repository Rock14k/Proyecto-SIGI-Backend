package com.sigi.servicio_ubicacion.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import com.sigi.servicio_ubicacion.dto.CoordenadasResponse;
import com.sigi.servicio_ubicacion.model.Geocache;
import com.sigi.servicio_ubicacion.repository.GeocacheRepository;

@ExtendWith(MockitoExtension.class)
class UbicacionServiceTest {

    @Mock
    private GeocacheRepository geocacheRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private UbicacionService ubicacionService;

    @BeforeEach
    @SuppressWarnings("unused")
    void configureOpenCageApiKey() {
        ReflectionTestUtils.setField(ubicacionService, "openCageApiKey", "clave-falsa-test");
    }

    @Test
    @DisplayName("Si existe caché, no llama a OpenCage")
    void desdeCacheSinLlamarExterno() {
        String dir = "Av. Siempre Viva 742, Valle del Sol";
        String norm = UbicacionService.normalizarDireccion(dir);
        Geocache g = new Geocache(norm, -33.45, -70.65);
        when(geocacheRepository.findByDireccionNormalizada(norm)).thenReturn(Optional.of(g));

        CoordenadasResponse r = ubicacionService.obtenerCoordenadas(dir);

        assertEquals(-33.45, r.getLatitud());
        assertEquals(-70.65, r.getLongitud());
        verify(restTemplate, never()).getForObject(anyString(), any(Class.class));
    }

    @Test
    @DisplayName("Sin API key retorna coordenadas nulas y mensaje claro")
    void sinApiKey() {
        ReflectionTestUtils.setField(ubicacionService, "openCageApiKey", "");
        when(geocacheRepository.findByDireccionNormalizada(anyString())).thenReturn(Optional.empty());

        CoordenadasResponse r = ubicacionService.obtenerCoordenadas("calle falsa 123");

        assertNull(r.getLatitud());
        assertNull(r.getLongitud());
    }

    @Test
    @DisplayName("Parsea respuesta de OpenCage y guarda en BD")
    void openCageExitoso() {
        when(geocacheRepository.findByDireccionNormalizada(anyString())).thenReturn(Optional.empty());

        Map<String, Object> geometry = Map.of("lat", -33.0, "lng", -71.0);
        Map<String, Object> resultado = Map.of("geometry", geometry);
        Map<String, Object> body = Map.of("results", List.of(resultado));
        when(restTemplate.getForObject(anyString(), any(Class.class))).thenReturn(body);

        CoordenadasResponse r = ubicacionService.obtenerCoordenadas("plaza de armas");

        assertEquals(-33.0, r.getLatitud());
        assertEquals(-71.0, r.getLongitud());
        verify(geocacheRepository).save(any(Geocache.class));
    }
}
