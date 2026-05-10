package com.sigi.eureka;
// Importamos las anotaciones necesarias
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

// @SpringBootApplication le dice a Spring que esta es la clase principal
// Combina @Configuration, @EnableAutoConfiguration y @ComponentScan
@SpringBootApplication
// @EnableEurekaServer convierte esta aplicación en un servidor de registro
// Los demás microservicios van a "registrarse" aquí para que se conozcan entre sí
@EnableEurekaServer
public class EurekaApplication {

	public static void main(String[] args) {
		SpringApplication.run(EurekaApplication.class, args);
	}

}
