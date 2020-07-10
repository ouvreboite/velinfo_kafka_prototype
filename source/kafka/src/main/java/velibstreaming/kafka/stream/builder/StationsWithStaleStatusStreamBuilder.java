package velibstreaming.kafka.stream.builder;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.ValueJoiner;
import velibstreaming.avro.record.stream.AvroDailyStationStats;
import velibstreaming.avro.record.stream.AvroStationChange;
import velibstreaming.avro.record.stream.AvroStationStats;
import velibstreaming.kafka.stream.StreamUtils;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;

public class StationsWithStaleStatusStreamBuilder {

    public KStream<String, AvroStationChange> build(KStream<String, AvroDailyStationStats> dailyStationStats, KStream<String, AvroStationChange> stationsWithStaleTimestampStream) {
        var lastDailyStationStatsTable = dailyStationStats.toTable(Materialized.with(Serdes.String(), StreamUtils.AvroSerde()));
        return stationsWithStaleTimestampStream
                .leftJoin(lastDailyStationStatsTable,computeStatus());
    }

    private ValueJoiner<AvroStationChange, AvroDailyStationStats, AvroStationChange> computeStatus() {
        return (stationChange, stationStats) -> {
            if(stationStats == null)
                return stationChange;
            if(stationChange.getStaleSinceTimestamp() == null)
                return stationChange;

            if(isStationStaleForMoreThanThreshold(stationChange.getAvailabilityTimestamp(), stationStats)){
                stationChange.setStaleStatus(">threshold");
                System.out.println(">threshold for "+stationChange.getStationName()+" = "+new Date(stationChange.getStaleSinceTimestamp()));
                return stationChange;
            }

            return stationChange;
        };
    }

    private boolean isStationStaleForMoreThanThreshold(long staleSinceTimestamp, AvroDailyStationStats stationStats) {
        String hour = StreamUtils.getHour(staleSinceTimestamp)+"";
        AvroStationStats hourlyStats = stationStats.getHourlyStats().get(hour);
        int staleThresholdInMinutes = getThresholdInMinutes(hourlyStats);

        LocalDateTime staleSince = StreamUtils.toLocalDateTime(staleSinceTimestamp);
        long staleSinceMinutes = ChronoUnit.MINUTES.between(staleSince, StreamUtils.now());

        return staleSinceMinutes > staleThresholdInMinutes;
    }

    /** depending of the traffic previous day at same hour, threshold is between 6h (lot of traffic) and 12h(no traffic) */
    private int getThresholdInMinutes( AvroStationStats hourlyStats) {
        if(hourlyStats == null)
            return 12*60;

        int totalBikeMovementLastDayAtSameHour = hourlyStats.getNumberOfElectricBikesRented()
                +hourlyStats.getNumberOfMechanicalBikesRented()
                +hourlyStats.getNumberOfElectricBikesReturned()
                +hourlyStats.getNumberOfMechanicalBikesReturned();

        int averageBetweenTwoMovementsLastDayAtSameHourInMinutes = totalBikeMovementLastDayAtSameHour == 0 ? 60 : 60 / totalBikeMovementLastDayAtSameHour;

        return 6*60 + 6*averageBetweenTwoMovementsLastDayAtSameHourInMinutes;
    }
}
