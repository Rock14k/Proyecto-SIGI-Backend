package com.sigi.servicio_notificacion.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sigi.servicio_notificacion.dto.AlertaEmergenciaRequest;
import com.sigi.servicio_notificacion.dto.NotificacionResponse;
import com.sigi.servicio_notificacion.service.NotificacionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/notificaciones")
@Tag(name = "Notificaciones", description = "Alertas ciudadanas (registro en BD)")
public class NotificacionController {

    private final NotificacionService notificacionService;

    public NotificacionController(NotificacionService notificacionService) {
        this.notificacionService = notificacionService;
    }

    @PostMapping("/alerta-emergencia")
    @Operation(summary = "Registrar alertas por nueva emergencia validada")
    public ResponseEntity<String> alertaEmergencia(@Valid @RequestBody AlertaEmergenciaRequest body) {
        int n = notificacionService.crearAlertaPorEmergencia(body);
        return ResponseEntity.ok("Notificaciones guardadas: " + n);
    }

    @GetMapping("/usuario/{usuarioId}")
    @Operation(summary = "Historial de notificaciones de un usuario")
    public ResponseEntity<List<NotificacionResponse>> porUsuario(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(notificacionService.listarPorUsuario(usuarioId));
    }
}
