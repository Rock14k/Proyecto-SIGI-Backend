package com.sigi.servicio_emergencia;

import org.springframework.boot.SpringApplication;

public class TestServicioEmergenciaApplication {

	public static void main(String[] args) {
		SpringApplication.from(ServicioEmergenciaApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
