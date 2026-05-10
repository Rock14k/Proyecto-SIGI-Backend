package com.sigi.servicio_reporte;

import org.springframework.boot.SpringApplication;

public class TestServicioReporteApplication {

	public static void main(String[] args) {
		SpringApplication.from(ServicioReporteApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
