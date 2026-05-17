package com.sigi.servicio_ubicacion.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Lo que consumen otros microservicios (ej. servicio-reporte vía Feign).
 * Campos claros para json: latitud, longitud, mensaje.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CoordenadasResponse {

    private Double latitud;
    private Double longitud;
    /** Explica si vino de caché, de OpenCage o falló */
    private String mensaje;
}
