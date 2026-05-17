package com.sigi.servicio_empleo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestClientConfig {

    @Bean
    @SuppressWarnings("unused")
    RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
