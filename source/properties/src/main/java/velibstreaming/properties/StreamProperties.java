package velibstreaming.properties;

import lombok.Getter;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Properties;

@Getter
public final class StreamProperties {
    private static StreamProperties INSTANCE = null;
    public static synchronized StreamProperties getInstance() {
        if(INSTANCE == null) {
            try {
                INSTANCE = new StreamProperties();
            } catch (IOException | IllegalAccessException e) {
                throw new IllegalStateException("Unable to collect properties",e);
            }
        }
        return INSTANCE;
    }

    private final long bikeLockEstimationDurationHours;

    private final String stationAvailabilityTopic;
    private final String bicycleCountTopic;

    private final String stationUpdatesTopic;
    private final String hourlyStationStatsTopic;
    private final String bikesLockedTopic;
    private final String stationStatusTopic;


    private final String bootstrapServers;
    private final String schemaRegistryUrl;

    private StreamProperties() throws IOException, IllegalAccessException {
        var props = new Properties();
        props.load(StreamProperties.class.getClassLoader().getResourceAsStream("stream.properties"));

        this.bikeLockEstimationDurationHours = Long.parseLong(props.getProperty("BikeLockEstimation.Hours", "24"));

        this.stationAvailabilityTopic = props.getProperty("StationAvailability.Topic");
        this.bicycleCountTopic = props.getProperty("BicycleCounts.Topic");

        this.stationUpdatesTopic = props.getProperty("StationUpdates.Topic");
        this.hourlyStationStatsTopic = props.getProperty("HourlyStationStats.Topic");
        this.bikesLockedTopic = props.getProperty("BikesLocked.Topic");
        this.stationStatusTopic = props.getProperty("StationStatus.Topic");

        this.bootstrapServers = props.getProperty("bootstrap.servers");
        this.schemaRegistryUrl = props.getProperty("schema.registry.url");

        checkNotEmpty();
    }

    private void checkNotEmpty() throws IllegalAccessException {
        for (Field field : StreamProperties.class.getFields()) {
            System.out.println(field);
            if(field.get(this) == null)
                throw new IllegalArgumentException("No value for property "+field.getName());
        }

    }
}
