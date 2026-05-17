package com.sigi.servicio_empleo.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class UsuarioConsultaService {

    private final RestTemplate restTemplate;

    @Value("${usuario.service.url:http://servicio-usuario:8081}")
    private String usuarioServiceUrl;

    public UsuarioConsultaService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @SuppressWarnings("unchecked")
    public DatosPostulante obtenerDatos(Long usuarioId) {
        try {
            Map<String, Object> body = restTemplate.getForObject(
                    usuarioServiceUrl + "/api/usuarios/" + usuarioId,
                    Map.class);
            if (body == null) {
                return new DatosPostulante(null, null, null, null);
            }
            String nombre = (String) body.get("nombre");
            String apellido = (String) body.get("apellido");
            String email = (String) body.get("email");
            String rut = (String) body.get("rut");
            return new DatosPostulante(nombre, apellido, email, rut);
        } catch (RuntimeException e) {
            return new DatosPostulante(null, null, null, null);
        }
    }

    public record DatosPostulante(String nombre, String apellido, String email, String rut) {}
}
