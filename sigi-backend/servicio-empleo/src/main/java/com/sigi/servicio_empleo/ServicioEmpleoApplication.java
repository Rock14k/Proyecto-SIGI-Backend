package com.sigi.servicio_empleo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class ServicioEmpleoApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServicioEmpleoApplication.class, args);
    }
}
