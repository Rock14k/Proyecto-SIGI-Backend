package com.sigi.servicio_reporte.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.sigi.servicio_reporte.client.dto.CoordenadasDto;

@FeignClient(name = "servicio-ubicacion", path = "/api/ubicaciones")
public interface UbicacionClient {

    @GetMapping("/coordenadas")
    CoordenadasDto obtenerCoordenadas(@RequestParam("direccion") String direccion);
}
