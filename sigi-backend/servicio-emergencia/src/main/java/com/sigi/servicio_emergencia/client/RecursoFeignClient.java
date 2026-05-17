package com.sigi.servicio_emergencia.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.sigi.servicio_emergencia.client.dto.AsignacionRecursoFeignRequest;
import com.sigi.servicio_emergencia.client.dto.AsignacionRecursoFeignResponse;

@FeignClient(name = "servicio-recurso", path = "/api/recursos")
public interface RecursoFeignClient {

    @PostMapping("/asignar-emergencia")
    AsignacionRecursoFeignResponse asignar(@RequestBody AsignacionRecursoFeignRequest body);

    @PostMapping("/liberar-emergencia/{emergenciaId}")
    void liberar(@PathVariable("emergenciaId") Long emergenciaId);
}
