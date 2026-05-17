package com.sigi.servicio_reporte.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.sigi.servicio_reporte.client.dto.CrearEmergenciaFeignRequest;
import com.sigi.servicio_reporte.client.dto.EmergenciaFeignResponse;

@FeignClient(name = "servicio-emergencia", path = "/api/emergencias")
public interface EmergenciaClient {

    @PostMapping("/desde-reporte")
    EmergenciaFeignResponse crearDesdeReporte(@RequestBody CrearEmergenciaFeignRequest body);
}
