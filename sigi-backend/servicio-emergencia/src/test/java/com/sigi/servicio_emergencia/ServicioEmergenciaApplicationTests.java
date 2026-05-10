package com.sigi.servicio_emergencia;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class ServicioEmergenciaApplicationTests {

	@Test
	void contextLoads() {
	}

}
