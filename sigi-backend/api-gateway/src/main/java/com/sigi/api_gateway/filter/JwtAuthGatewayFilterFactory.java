package com.sigi.api_gateway.filter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import java.nio.charset.StandardCharsets;
import java.security.Key;

/**
 * En application.yml el filtro se llama "JwtAuth" porque Spring quita el sufijo
 * "GatewayFilterFactory" del nombre de la clase (convención Spring Cloud Gateway).
 */
@Component
public class JwtAuthGatewayFilterFactory extends AbstractGatewayFilterFactory<JwtAuthGatewayFilterFactory.Config> {

    @Value("${jwt.secret}")
    private String secretKey;

    public JwtAuthGatewayFilterFactory() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {

        return (exchange, chain) -> {

            String authHeader = exchange.getRequest()
                    .getHeaders()
                    .getFirst(HttpHeaders.AUTHORIZATION);

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {

                exchange.getResponse()
                        .setStatusCode(HttpStatus.UNAUTHORIZED);

                return exchange.getResponse().setComplete();
            }

            String token = authHeader.substring(7);

            try {

                Claims claims = validateToken(token);

                Object uid = claims.get("userId");
                String userIdHeader = uid != null ? uid.toString() : "";

                var reqBuilder = exchange.getRequest()
                        .mutate()
                        .header("X-User-Name", claims.getSubject())
                        .header("X-User-Role", claims.get("role", String.class));
                if (!userIdHeader.isEmpty()) {
                    reqBuilder.header("X-User-Id", userIdHeader);
                }
                Object nombre = claims.get("nombre");
                Object apellido = claims.get("apellido");
                if (nombre != null) {
                    reqBuilder.header("X-User-Nombre", nombre.toString());
                }
                if (apellido != null) {
                    reqBuilder.header("X-User-Apellido", apellido.toString());
                }

                ServerWebExchange modifiedExchange = exchange.mutate()
                        .request(reqBuilder.build())
                        .build();

                return chain.filter(modifiedExchange);

            } catch (Exception e) {

                exchange.getResponse()
                        .setStatusCode(HttpStatus.UNAUTHORIZED);

                return exchange.getResponse().setComplete();
            }
        };
    }

    private Claims validateToken(String token) {

        Key key = Keys.hmacShaKeyFor(
                secretKey.getBytes(StandardCharsets.UTF_8)
        );

        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public static class Config {

    }
}
