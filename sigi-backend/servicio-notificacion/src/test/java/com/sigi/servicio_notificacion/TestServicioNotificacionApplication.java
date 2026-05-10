package com.sigi.servicio_notificacion;

import org.springframework.boot.SpringApplication;

public class TestServicioNotificacionApplication {

	public static void main(String[] args) {
		SpringApplication.from(ServicioNotificacionApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
