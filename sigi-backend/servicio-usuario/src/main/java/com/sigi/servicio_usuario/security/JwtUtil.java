package com.sigi.servicio_usuario.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

// @Component registra esta clase como un Bean de Spring (patrón Singleton:
// Spring mantiene una sola instancia compartida en la aplicación)
@Component
public class JwtUtil {

    /** Claim extra: ID numérico del usuario para otros microservicios sin consultar BD */
    public static final String CLAIM_USER_ID = "userId";
    public static final String CLAIM_NOMBRE = "nombre";
    public static final String CLAIM_APELLIDO = "apellido";

    // Misma clave que api-gateway (variable JWT_SECRET en Docker / Kubernetes)
    @Value("${jwt.secret:sigi-municipalidad-valle-sol-secret-key-2024-segura}")
    private String secretKey;
    
    // El token dura 24 horas (en milisegundos)
    // 1000ms × 60s × 60min × 24h = 86,400,000 ms
    private static final long EXPIRATION_TIME = 86400000L;

    // Método que obtiene la clave de firma (la convertimos a formato criptográfico)
    private Key getSigningKey() {
        // getBytes convierte el String a bytes
        // Keys.hmacShaKeyFor() crea una clave HMAC-SHA compatible con JWT
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Genera un JWT. Incluimos userId (además de email en subject y role) para que
     * servicio-reporte guarde usuario_id sin otra llamada HTTP al servicio-usuario.
     */
    public String generateToken(String email, String rol, Long userId, String nombre, String apellido) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", rol);
        if (userId != null) {
            claims.put(CLAIM_USER_ID, userId);
        }
        if (nombre != null) {
            claims.put(CLAIM_NOMBRE, nombre);
        }
        if (apellido != null) {
            claims.put(CLAIM_APELLIDO, apellido);
        }

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

    public Long extractUserId(String token) {
        return extractClaims(token).get(CLAIM_USER_ID, Long.class);
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