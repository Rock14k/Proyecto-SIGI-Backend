package com.sigi.servicio_ubicacion;

import org.springframework.boot.SpringApplication;

public class TestServicioUbicacionApplication {

	public static void main(String[] args) {
		SpringApplication.from(ServicioUbicacionApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
