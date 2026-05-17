package com.sigi.servicio_recurso;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class ServicioRecursoApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServicioRecursoApplication.class, args);
	}

}
