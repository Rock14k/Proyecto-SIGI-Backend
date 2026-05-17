package com.sigi.servicio_emergencia.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.sigi.servicio_emergencia.client.dto.AlertaEmergenciaFeignRequest;

@FeignClient(name = "servicio-notificacion", path = "/api/notificaciones")
public interface NotificacionFeignClient {

    @PostMapping("/alerta-emergencia")
    String crearAlerta(@RequestBody AlertaEmergenciaFeignRequest body);
}
