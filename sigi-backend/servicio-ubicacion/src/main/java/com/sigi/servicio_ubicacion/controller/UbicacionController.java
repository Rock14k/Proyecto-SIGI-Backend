package com.sigi.servicio_ubicacion.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sigi.servicio_ubicacion.dto.CoordenadasResponse;
import com.sigi.servicio_ubicacion.service.UbicacionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/ubicaciones")
@Tag(name = "Ubicación", description = "Geocodificación de direcciones (OpenCage + caché)")
public class UbicacionController {

    private final UbicacionService ubicacionService;

    public UbicacionController(UbicacionService ubicacionService) {
        this.ubicacionService = ubicacionService;
    }

    @GetMapping("/coordenadas")
    @Operation(summary = "Obtener latitud y longitud", description = "Convierte una dirección textual en coordenadas GPS")
    public ResponseEntity<CoordenadasResponse> coordenadas(@RequestParam("direccion") String direccion) {
        return ResponseEntity.ok(ubicacionService.obtenerCoordenadas(direccion));
    }
}
