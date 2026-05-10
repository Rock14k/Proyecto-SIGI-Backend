package com.sigi.servicio_ubicacion;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class ServicioUbicacionApplicationTests {

	@Test
	void contextLoads() {
	}

}
