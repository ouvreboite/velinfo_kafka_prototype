package fr.velinfo.kafka.sink;

import fr.velinfo.properties.Topics;
import fr.velinfo.repository.HourlyStationStatsRepository;

public class SinkApplication {

    public static void main(final String[] args) {
        new Thread(() -> new DbSink<>(Topics.HOURLY_STATION_STATS, new HourlyStationStatsRepository()).pollAndSink()).start();
    }
}
