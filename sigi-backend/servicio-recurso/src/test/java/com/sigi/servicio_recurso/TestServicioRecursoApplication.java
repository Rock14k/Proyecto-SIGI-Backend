package com.sigi.servicio_recurso;

import org.springframework.boot.SpringApplication;

public class TestServicioRecursoApplication {

	public static void main(String[] args) {
		SpringApplication.from(ServicioRecursoApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
