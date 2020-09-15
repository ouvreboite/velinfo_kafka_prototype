package fr.velinfo.repository;

import fr.velinfo.properties.ConnectionConfiguration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionUtils {
    public static Connection getConnection() throws SQLException {
        ConnectionConfiguration config = ConnectionConfiguration.getInstance();
        return DriverManager.getConnection(
                config.getDatabaseUrl(),
                config.getDatabaseUser(),
                config.getDatabasePassword()
        );
    }
}
