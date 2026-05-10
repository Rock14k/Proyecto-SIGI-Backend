package com.sigi.servicio_usuario.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

// @Component registra esta clase como un Bean de Spring
// Los Beans son objetos que Spring crea y administra automáticamente
@Component
public class JwtUtil {

    // Clave secreta para firmar los tokens
    // Debe ser la MISMA que usa el API Gateway para validar
    private static final String SECRET_KEY = "sigi-municipalidad-valle-sol-secret-key-2024-segura";
    
    // El token dura 24 horas (en milisegundos)
    // 1000ms × 60s × 60min × 24h = 86,400,000 ms
    private static final long EXPIRATION_TIME = 86400000L;

    // Método que obtiene la clave de firma (la convertimos a formato criptográfico)
    private Key getSigningKey() {
        // getBytes convierte el String a bytes
        // Keys.hmacShaKeyFor() crea una clave HMAC-SHA compatible con JWT
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
    }

    // Método para generar un token JWT para un usuario
    // email = quién es el usuario
    // rol = qué permisos tiene
    public String generateToken(String email, String rol) {
        // Map para guardar datos adicionales en el token (llamados "claims")
        Map<String, Object> claims = new HashMap<>();
        // Guardamos el rol en el token para que el Gateway lo pueda leer
        claims.put("role", rol);

        return Jwts.builder()
                // setClaims() agrega los datos extras al token
                .setClaims(claims)
                // setSubject() define quién es el dueño del token
                .setSubject(email)
                // setIssuedAt() guarda cuándo se creó el token
                .setIssuedAt(new Date(System.currentTimeMillis()))
                // setExpiration() define cuándo expira el token
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                // signWith() firma el token con nuestra clave secreta y algoritmo HS256
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                // compact() genera el String del token en formato "xxxxx.yyyyy.zzzzz"
                .compact();
    }

    // Extrae el email (subject) de un token
    public String extractEmail(String token) {
        return extractClaims(token).getSubject();
    }

    // Extrae el rol del token
    public String extractRol(String token) {
        return extractClaims(token).get("role", String.class);
    }

    // Verifica si el token está expirado
    public boolean isTokenExpired(String token) {
        // before() verifica si la fecha de expiración ya pasó
        return extractClaims(token).getExpiration().before(new Date());
    }

    // Valida que el token corresponda al usuario y no esté expirado
    public boolean validateToken(String token, String email) {
        // Extraemos el email del token y lo comparamos con el email dado
        String tokenEmail = extractEmail(token);
        // El token es válido si el email coincide Y no está expirado
        return tokenEmail.equals(email) && !isTokenExpired(token);
    }

    // Método privado que parsea el token y retorna todos los claims
    private Claims extractClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)  // Valida y parsea el token
                .getBody();             // Obtiene el cuerpo (los datos)
    }
}