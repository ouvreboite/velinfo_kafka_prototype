package fr.velinfo.kafka.sink;

import fr.velinfo.properties.StreamProperties;
import fr.velinfo.repository.HourlyStationStatsRepository;

public class SinkApplication {

    public static void main(final String[] args) {
        new Thread(() -> {
            new DbSink<>(StreamProperties.getInstance().getHourlyStationStatsTopic(), new HourlyStationStatsRepository()).pollAndSink();
        }).start();
    }
}
