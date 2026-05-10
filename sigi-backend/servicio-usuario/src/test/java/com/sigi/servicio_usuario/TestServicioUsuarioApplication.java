package com.sigi.servicio_usuario;

import org.springframework.boot.SpringApplication;

public class TestServicioUsuarioApplication {

	public static void main(String[] args) {
		SpringApplication.from(ServicioUsuarioApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
