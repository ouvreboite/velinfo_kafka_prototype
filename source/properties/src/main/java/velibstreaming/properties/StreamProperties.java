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

    private final long availabilityPeriodSeconds;
    private final long bicycleCountPeriodSeconds;
    private final long counterCharacteristicsPeriodSeconds;

    private final String stationAvailabilityTopic;
    private final String bicycleCountTopic;
    private final String counterCharacteristicsTopic;

    private final String stationChangesTopic;
    private final String stationChangesWithStaleTimestampTopic;
    private final String stationChangesWithStaleStatusTopic;
    private final String dailyStationStatsTopic;

    private final String bootstrapServers;
    private final String schemaRegistryUrl;

    private StreamProperties() throws IOException, IllegalAccessException {
        var props = new Properties();
        props.load(StreamProperties.class.getClassLoader().getResourceAsStream("stream.properties"));

        this.availabilityPeriodSeconds = Long.parseLong(props.getProperty("StationAvailability.Loop.Seconds", "60"));
        this.bicycleCountPeriodSeconds = Long.parseLong(props.getProperty("BicycleCounterCharacteristics.Loop.Seconds", "60"));
        this.counterCharacteristicsPeriodSeconds = Long.parseLong(props.getProperty("BicycleCount.Loop.Seconds", "60"));

        this.stationAvailabilityTopic = props.getProperty("StationAvailability.Topic");
        this.bicycleCountTopic = props.getProperty("BicycleCounterCharacteristics.Topic");
        this.counterCharacteristicsTopic = props.getProperty("BicycleCount.Topic");

        this.stationChangesTopic = props.getProperty("StationChanges.Topic");
        this.stationChangesWithStaleTimestampTopic = props.getProperty("StationChangesWithStaleTimestamp.Topic");
        this.stationChangesWithStaleStatusTopic = props.getProperty("StationChangesWithStaleStatus.Topic");
        this.dailyStationStatsTopic = props.getProperty("DailyStationStats.Topic");

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
