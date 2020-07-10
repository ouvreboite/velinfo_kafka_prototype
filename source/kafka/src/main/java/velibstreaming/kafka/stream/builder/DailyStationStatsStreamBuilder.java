package velibstreaming.kafka.stream.builder;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.state.WindowStore;
import velibstreaming.avro.record.stream.AvroDailyStationStats;
import velibstreaming.avro.record.stream.AvroStationChange;
import velibstreaming.avro.record.stream.AvroStationStats;
import velibstreaming.kafka.stream.StreamUtils;

import java.time.Duration;
import java.util.HashMap;

import static org.apache.kafka.streams.kstream.Suppressed.BufferConfig.unbounded;

public class DailyStationStatsStreamBuilder {

    public KStream<String, AvroDailyStationStats> build(KStream<String, AvroStationChange> stationChangesStream){
        return BuildDailyStatsStream(buildHourlyStatsStream(stationChangesStream));
    }

    private KStream<String, AvroStationStats> buildHourlyStatsStream(KStream<String, AvroStationChange> stationChangesStream) {
        TimeWindows hourWindow = TimeWindows.of(Duration.ofHours(1)).grace(Duration.ofMinutes(5));
        var materializedHour = Materialized.<String, AvroStationStats, WindowStore<Bytes, byte[]>>with(Serdes.String(), StreamUtils.AvroSerde())
                .withRetention(Duration.ofHours(6));

        return stationChangesStream
                .groupByKey(Grouped.with(Serdes.String(), StreamUtils.AvroSerde()))
                .windowedBy(hourWindow)
                .aggregate(AvroStationStats::new,
                        ComputeHourlyStats(),
                        materializedHour)
                .suppress(Suppressed.untilWindowCloses(unbounded()))
                .toStream()
                .map((windowKey, stationStats) -> {
                    stationStats.setPeriodStart(windowKey.window().start());
                    stationStats.setPeriodEnd(windowKey.window().end());
                    return new KeyValue<>(windowKey.key(), stationStats);
                });
    }

    private KStream<String, AvroDailyStationStats> BuildDailyStatsStream(KStream<String, AvroStationStats> hourlyStatsStream) {
        TimeWindows dailyWindow = TimeWindows.of(Duration.ofDays(1)).grace(Duration.ofMinutes(5));
        var materializedDay = Materialized.<String, AvroDailyStationStats, WindowStore<Bytes, byte[]>>with(Serdes.String(), StreamUtils.AvroSerde())
                .withRetention(Duration.ofDays(2));

        return hourlyStatsStream
                .groupByKey(Grouped.with(Serdes.String(), StreamUtils.AvroSerde()))
                .windowedBy(dailyWindow)
                .aggregate(() -> new AvroDailyStationStats(new HashMap<>(), new AvroStationStats()),
                        ComputeDailyStats(),
                        materializedDay)
                .suppress(Suppressed.untilWindowCloses(unbounded()))
                .toStream()
                .map((windowKey, dailyStationStats) -> {
                    dailyStationStats.getDailyStats().setPeriodStart(windowKey.window().start());
                    dailyStationStats.getDailyStats().setPeriodEnd(windowKey.window().end());
                    return new KeyValue<>(windowKey.key(), dailyStationStats);
                });
    }
    private Aggregator<String, AvroStationChange, AvroStationStats> ComputeHourlyStats() {
        return (key, stationChange, stats) -> {
            int deltaElectric = stationChange.getElectricBikesAtStation() - stats.getLastNumberOfElectricBikes();
            if(deltaElectric > 0)
                stats.setNumberOfElectricBikesReturned(stats.getNumberOfElectricBikesReturned()+deltaElectric);
            else
                stats.setNumberOfElectricBikesRented(stats.getNumberOfElectricBikesRented()-deltaElectric);

            int deltaMechanical = stationChange.getMechanicalBikesAtStation() - stats.getLastNumberOfMechanicalBikes();
            if(deltaMechanical > 0)
                stats.setNumberOfMechanicalBikesReturned(stats.getNumberOfMechanicalBikesReturned()+deltaMechanical);
            else
                stats.setNumberOfMechanicalBikesRented(stats.getNumberOfMechanicalBikesRented()-deltaMechanical);

            stats.setLastNumberOfElectricBikes(stationChange.getElectricBikesAtStation());
            stats.setLastNumberOfMechanicalBikes(stationChange.getMechanicalBikesAtStation());
            stats.setLastLoadTimestamp(stationChange.getLoadTimestamp());
            return stats;
        };
    }

    private Aggregator<String, AvroStationStats, AvroDailyStationStats> ComputeDailyStats() {
        return (key, hourlyStats, dailyStats) -> {

            String hour = StreamUtils.getHour(hourlyStats.getPeriodStart())+"";
            dailyStats.getHourlyStats().put(hour, hourlyStats);

            AvroStationStats stats = dailyStats.getDailyStats();
            stats.setNumberOfElectricBikesRented(stats.getNumberOfElectricBikesRented()+hourlyStats.getNumberOfElectricBikesRented());
            stats.setNumberOfMechanicalBikesRented(stats.getNumberOfMechanicalBikesRented()+hourlyStats.getNumberOfMechanicalBikesRented());
            stats.setNumberOfElectricBikesReturned(stats.getNumberOfElectricBikesReturned()+hourlyStats.getNumberOfElectricBikesReturned());
            stats.setNumberOfMechanicalBikesReturned(stats.getNumberOfMechanicalBikesReturned()+hourlyStats.getNumberOfMechanicalBikesReturned());

            stats.setLastNumberOfElectricBikes(hourlyStats.getLastNumberOfElectricBikes());
            stats.setLastNumberOfMechanicalBikes(hourlyStats.getLastNumberOfMechanicalBikes());
            stats.setLastLoadTimestamp(hourlyStats.getLastLoadTimestamp());
            return dailyStats;
        };
    }
}
