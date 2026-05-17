package com.sigi.servicio_reporte;

import org.junit.jupiter.api.Test;

/**
 * Evitamos @SpringBootTest con Testcontainers (requiere Docker en CI).
 * La lógica se prueba en {@link com.sigi.servicio_reporte.service.ReporteServiceTest}.
 */
class ServicioReporteApplicationTests {

    @Test
    void proyectoTienePruebasUnitarias() {
        // Placeholder: mvn test ejecuta ReporteServiceTest
    }
}
