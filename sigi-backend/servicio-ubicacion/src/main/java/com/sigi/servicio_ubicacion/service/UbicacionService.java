package com.sigi.servicio_ubicacion.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.sigi.servicio_ubicacion.dto.CoordenadasResponse;
import com.sigi.servicio_ubicacion.model.Geocache;
import com.sigi.servicio_ubicacion.repository.GeocacheRepository;

/**
 * Llama a OpenCage y guarda filas en MySQL para no repetir la misma consulta.
 */
@Service
public class UbicacionService {

    private final GeocacheRepository geocacheRepository;
    private final RestTemplate restTemplate;

    @Value("${opencage.api.key:}")
    private String openCageApiKey;

    public UbicacionService(GeocacheRepository geocacheRepository, RestTemplate restTemplate) {
        this.geocacheRepository = geocacheRepository;
        this.restTemplate = restTemplate;
    }

    public CoordenadasResponse obtenerCoordenadas(String direccion) {
        if (direccion == null || direccion.isBlank()) {
            return new CoordenadasResponse(null, null, "Dirección vacía");
        }

        String clave = normalizarDireccion(direccion);
        return geocacheRepository.findByDireccionNormalizada(clave)
                .map(g -> new CoordenadasResponse(g.getLatitud(), g.getLongitud(), "Desde caché local"))
                .orElseGet(() -> consultarOpenCageGuardar(clave, direccion));
    }

    /** Visible para tests: misma regla que usa el servicio */
    static String normalizarDireccion(String s) {
        return s.trim().toLowerCase().replaceAll("\\s+", " ");
    }

    @SuppressWarnings("unchecked")
    private CoordenadasResponse consultarOpenCageGuardar(String claveNormalizada, String direccionOriginal) {
        if (openCageApiKey == null || openCageApiKey.isBlank()) {
            return new CoordenadasResponse(null, null, "API key OpenCage no configurada (variable OPENCAGE_API_KEY)");
        }

        String url = UriComponentsBuilder
                .fromUriString("https://api.opencagedata.com/geocode/v1/json")
                .queryParam("q", direccionOriginal)
                .queryParam("key", openCageApiKey)
                .queryParam("language", "es")
                .queryParam("limit", "1")
                .build()
                .toUriString();

        Map<String, Object> cuerpo = restTemplate.getForObject(url, Map.class);
        if (cuerpo == null) {
            return new CoordenadasResponse(null, null, "Respuesta vacía de OpenCage");
        }

        List<?> resultados = (List<?>) cuerpo.get("results");
        if (resultados == null || resultados.isEmpty()) {
            return new CoordenadasResponse(null, null, "Sin resultados para esa dirección");
        }

        Map<String, Object> primero = (Map<String, Object>) resultados.get(0);
        Map<String, Object> geometria = (Map<String, Object>) ((Map<String, Object>) primero.get("geometry"));
        if (geometria == null) {
            return new CoordenadasResponse(null, null, "Sin geometría en respuesta");
        }

        Number lat = (Number) geometria.get("lat");
        Number lng = (Number) geometria.get("lng");
        if (lat == null || lng == null) {
            return new CoordenadasResponse(null, null, "Coordenadas incompletas");
        }

        double latD = lat.doubleValue();
        double lngD = lng.doubleValue();

        geocacheRepository.save(new Geocache(claveNormalizada, latD, lngD));
        return new CoordenadasResponse(latD, lngD, "Geocodificado con OpenCage");
    }
}
