package com.kakaoscan.server.infrastructure.logging;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class LogsStoreConnectionFactory {
    private static final DataSource dataSource;

    static {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(System.getenv("DATASOURCE_URL"));
        config.setUsername(System.getenv("DATASOURCE_USERNAME"));
        config.setPassword(System.getenv("DATASOURCE_PASSWORD"));
        config.setDriverClassName("com.mysql.cj.jdbc.Driver");
        config.setPoolName("HikariPool");
        config.setMaximumPoolSize(25);
        config.setMinimumIdle(10);
        config.setIdleTimeout(600000);
        config.setConnectionTimeout(30000);
        config.setMaxLifetime(1800000);
        config.setValidationTimeout(5000);
        config.setLeakDetectionThreshold(0);
        config.setConnectionInitSql("SET wait_timeout = 1830");

        dataSource = new HikariDataSource(config);
    }

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
}
