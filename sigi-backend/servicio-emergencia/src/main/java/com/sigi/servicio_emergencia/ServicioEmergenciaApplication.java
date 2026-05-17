package com.sigi.servicio_emergencia;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.sigi.servicio_emergencia.client")
public class ServicioEmergenciaApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServicioEmergenciaApplication.class, args);
	}

}
