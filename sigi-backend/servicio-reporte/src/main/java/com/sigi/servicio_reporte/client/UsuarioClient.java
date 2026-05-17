package com.sigi.servicio_reporte.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.sigi.servicio_reporte.client.dto.UsuarioFeignDto;

@FeignClient(name = "servicio-usuario", path = "/api/usuarios")
public interface UsuarioClient {

    @GetMapping("/{id}")
    UsuarioFeignDto obtenerPorId(@PathVariable("id") Long id);
}
