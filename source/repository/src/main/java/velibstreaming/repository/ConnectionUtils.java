package velibstreaming.repository;

import velibstreaming.properties.StreamProperties;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionUtils {
    public static Connection getConnection() throws SQLException {
        StreamProperties props = StreamProperties.getInstance();
        return DriverManager.getConnection(
                props.getDatabaseUrl(),
                props.getDatabaseUser(),
                props.getDatabasePassword()
        );
    }
}
