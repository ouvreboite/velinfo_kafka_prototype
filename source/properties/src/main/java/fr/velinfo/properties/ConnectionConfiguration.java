package fr.velinfo.properties;

import lombok.AccessLevel;
import lombok.Getter;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Properties;

@Getter
public final class ConnectionConfiguration {
    private static ConnectionConfiguration INSTANCE = null;
    public static synchronized ConnectionConfiguration getInstance() {
        if(INSTANCE == null) {
            try {
                INSTANCE = new ConnectionConfiguration();
            } catch (IOException | IllegalAccessException e) {
                throw new IllegalStateException("Unable to collect properties",e);
            }
        }
        return INSTANCE;
    }
    private final String bootstrapServers;
    private final String schemaRegistryUrl;
    private final String databaseUrl;
    private final String databaseUser;
    private final String databasePassword;

    @Getter(AccessLevel.NONE)
    private String mockSchemaRegistryUrl;

    private ConnectionConfiguration() throws IOException, IllegalAccessException {
        var props = new Properties();
        props.load(ConnectionConfiguration.class.getClassLoader().getResourceAsStream("stream.properties"));

        this.bootstrapServers = props.getProperty("bootstrap.servers");
        this.schemaRegistryUrl = props.getProperty("schema.registry.url");
        this.databaseUrl = props.getProperty("database.url");
        this.databaseUser = props.getProperty("database.user");
        this.databasePassword = props.getProperty("database.password");

        checkNotEmpty();
    }

    private void checkNotEmpty() throws IllegalAccessException {
        for (Field field : ConnectionConfiguration.class.getFields()) {
            System.out.println(field);
            if(field.get(this) == null)
                throw new IllegalArgumentException("No value for property "+field.getName());
        }
    }

    public void setMockSchemaRegistryUrl(String mockSchemaRegistryUrl){
        this.mockSchemaRegistryUrl = mockSchemaRegistryUrl;
    }

    public String getSchemaRegistryUrl(){
        return mockSchemaRegistryUrl == null ? schemaRegistryUrl : mockSchemaRegistryUrl;
    }
}
