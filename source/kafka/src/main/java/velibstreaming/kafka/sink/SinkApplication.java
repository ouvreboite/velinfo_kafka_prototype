package velibstreaming.kafka.sink;

import velibstreaming.repository.HourlyStationStatsRepository;
import velibstreaming.properties.StreamProperties;
import velibstreaming.repository.DailyNearbyTrafficRepository;

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
