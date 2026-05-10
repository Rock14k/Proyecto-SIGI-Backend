
package com.sigi.api_gateway.filter;

// Importaciones de Spring
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

// Importaciones JWT
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

// Importaciones Java
import java.nio.charset.StandardCharsets;
import java.security.Key;

// Spring creará automáticamente esta clase como componente
@Component
public class JwtAuthFilter extends AbstractGatewayFilterFactory<JwtAuthFilter.Config> {

    // Obtenemos la clave secreta desde application.yml
    @Value("${jwt.secret}")
    private String secretKey;

    // Constructor
    public JwtAuthFilter() {
        super(Config.class);
    }

    // Método principal del filtro
    @Override
    public GatewayFilter apply(Config config) {

        return (exchange, chain) -> {

            // Obtenemos el header Authorization
            String authHeader = exchange.getRequest()
                    .getHeaders()
                    .getFirst(HttpHeaders.AUTHORIZATION);

            // Validamos que exista el token Bearer
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {

                exchange.getResponse()
                        .setStatusCode(HttpStatus.UNAUTHORIZED);

                return exchange.getResponse().setComplete();
            }

            // Extraemos el token JWT
            String token = authHeader.substring(7);

            try {

                // Validamos el token
                Claims claims = validateToken(token);

                // Agregamos información del usuario a los headers
                ServerWebExchange modifiedExchange = exchange.mutate()
                        .request(
                                exchange.getRequest()
                                        .mutate()
                                        .header("X-User-Name", claims.getSubject())
                                        .header(
                                                "X-User-Role",
                                                claims.get("role", String.class)
                                        )
                                        .build()
                        )
                        .build();

                // Continuamos con la petición
                return chain.filter(modifiedExchange);

            } catch (Exception e) {

                // Token inválido
                exchange.getResponse()
                        .setStatusCode(HttpStatus.UNAUTHORIZED);

                return exchange.getResponse().setComplete();
            }
        };
    }

    // Método que valida el JWT
    private Claims validateToken(String token) {

        // Creamos la clave de firma
        Key key = Keys.hmacShaKeyFor(
                secretKey.getBytes(StandardCharsets.UTF_8)
        );

        // Validamos y parseamos el token
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // Clase de configuración requerida por Gateway
    public static class Config {

    }
}

