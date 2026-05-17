package com.sigi.servicio_usuario.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * Hibernate ddl-auto=update no amplía ENUM de MySQL cuando se agregan valores al enum Java.
 * Convierte {@code rol} a VARCHAR para soportar todos los roles actuales y futuros.
 */
@Component
@Order(0)
public class RolColumnMigration implements CommandLineRunner {

    private final JdbcTemplate jdbc;

    public RolColumnMigration(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public void run(String... args) {
        String columnType = jdbc.query(
                """
                SELECT DATA_TYPE
                FROM information_schema.COLUMNS
                WHERE TABLE_SCHEMA = DATABASE()
                  AND TABLE_NAME = 'usuarios'
                  AND COLUMN_NAME = 'rol'
                """,
                rs -> rs.next() ? rs.getString(1) : null);

        if (columnType == null) {
            return;
        }
        if ("enum".equalsIgnoreCase(columnType)) {
            jdbc.execute("ALTER TABLE usuarios MODIFY rol VARCHAR(30) NOT NULL");
        }
    }
}
