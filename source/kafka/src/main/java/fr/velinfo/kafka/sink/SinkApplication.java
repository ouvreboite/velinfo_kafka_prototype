package fr.velinfo.kafka.sink;

import fr.velinfo.repository.HourlyStationStatsRepository;
import fr.velinfo.properties.StreamProperties;
import fr.velinfo.repository.DailyNearbyTrafficRepository;

public class SinkApplication {

    public static void main(final String[] args) {
        new Thread(() -> {
            new DbSink<>(StreamProperties.getInstance().getHourlyStationStatsTopic(), new HourlyStationStatsRepository()).pollAndSink();
        }).start();
        new Thread(() -> {
            new DbSink<>(StreamProperties.getInstance().getDailyStationNearbyTrafficTopic(), new DailyNearbyTrafficRepository()).pollAndSink();
        }).start();
    }
}
