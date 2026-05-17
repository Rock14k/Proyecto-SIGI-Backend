package com.sigi.servicio_reporte.service;

import org.springframework.stereotype.Service;

import com.sigi.servicio_reporte.client.UbicacionClient;
import com.sigi.servicio_reporte.client.dto.CoordenadasDto;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

/**
 * Capa fina para envolver Feign con Circuit Breaker (Resilience4j).
 * Si ubicacion falla, el fallback devuelve coordenadas nulas y el reporte igual se guarda.
 */
@Service
public class UbicacionConsultaService {

    private final UbicacionClient ubicacionClient;

    public UbicacionConsultaService(UbicacionClient ubicacionClient) {
        this.ubicacionClient = ubicacionClient;
    }

    @CircuitBreaker(name = "ubicacion", fallbackMethod = "fallbackCoordenadas")
    public CoordenadasDto obtenerCoordenadas(String direccion) {
        return ubicacionClient.obtenerCoordenadas(direccion);
    }

    @SuppressWarnings("unused")
    private CoordenadasDto fallbackCoordenadas(String direccion, Throwable error) {
        CoordenadasDto dto = new CoordenadasDto();
        dto.setLatitud(null);
        dto.setLongitud(null);
        dto.setMensaje("No disponible");
        return dto;
    }
}
