package com.sigi.servicio_usuario.security;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

// @Configuration indica que esta clase tiene configuración de Spring
@Configuration
// @EnableWebSecurity activa la configuración de seguridad web
@EnableWebSecurity
public class SecurityConfig {

    // Configuración de qué rutas están protegidas y cuáles son públicas
    // @Bean le dice a Spring que el objeto que retorna debe ser administrado por él
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Desactivamos CSRF porque usamos JWT (no cookies)
            // CSRF es un tipo de ataque que aprovecha las cookies de sesión
            .csrf(csrf -> csrf.disable())
            
            // Configuramos qué peticiones requieren autenticación
            .authorizeHttpRequests(auth -> auth
                // Las rutas de autenticación son públicas (login, registro)
                .requestMatchers("/auth/**").permitAll()
                // La documentación Swagger también es pública
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                // Cualquier otra ruta requiere estar autenticado
                .anyRequest().authenticated()
            )
            
            // Configuramos el manejo de sesiones como STATELESS
            // Esto significa que NO guardamos sesiones en el servidor
            // Cada petición debe traer su propio token JWT
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            );

        return http.build();
    }

    // Bean para encriptar contraseñas con BCrypt
    // BCrypt es un algoritmo seguro que agrega "sal" aleatoria a cada contraseña
    @Bean
    public PasswordEncoder passwordEncoder() {
        // El número 12 es el "strength" (más alto = más seguro pero más lento)
        return new BCryptPasswordEncoder(12);
    }
}