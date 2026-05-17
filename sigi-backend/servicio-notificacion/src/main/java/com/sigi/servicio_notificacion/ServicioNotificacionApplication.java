package com.sigi.servicio_notificacion;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class ServicioNotificacionApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServicioNotificacionApplication.class, args);
	}

}
