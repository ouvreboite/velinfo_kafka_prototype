package fr.velinfo.common;

import lombok.Getter;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

@Getter
public final class ConnectionConfiguration {
    private final String bootstrapServers;
    private final String schemaRegistryUrl;
    private final String databaseUrl;
    private final String databaseUser;
    private final String databasePassword;

    public ConnectionConfiguration(Properties props) {
        this.bootstrapServers = props.getProperty("bootstrap.servers");
        this.schemaRegistryUrl = props.getProperty("schema.registry.url");
        this.databaseUrl = props.getProperty("database.url");
        this.databaseUser = props.getProperty("database.user");
        this.databasePassword = props.getProperty("database.password");
    }

    public ConnectionConfiguration(String path) throws IOException {
        this(propsFromFile(path));
    }

    private static Properties propsFromFile(String path) throws IOException {
        var props = new Properties();
        try(var fis = new FileInputStream(path)){
            props.load(fis);
        }
        return props;
    }
}
