package com.sigi.servicio_recurso;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class ServicioRecursoApplicationTests {

	@Test
	void contextLoads() {
	}

}
