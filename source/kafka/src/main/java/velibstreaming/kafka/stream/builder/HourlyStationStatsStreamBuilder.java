package velibstreaming.kafka.stream.builder;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.state.WindowStore;
import velibstreaming.avro.record.stream.AvroStationStats;
import velibstreaming.avro.record.stream.AvroStationUpdate;
import velibstreaming.kafka.stream.StreamUtils;

import java.time.Duration;

import static org.apache.kafka.streams.kstream.Suppressed.BufferConfig.unbounded;

public class HourlyStationStatsStreamBuilder {

    public KStream<String, AvroStationStats> build(KStream<String, AvroStationUpdate> stationChangesStream){
        return buildHourlyStatsStream(stationChangesStream);
    }

    private KStream<String, AvroStationStats> buildHourlyStatsStream(KStream<String, AvroStationUpdate> stationChangesStream) {
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

    private Aggregator<String, AvroStationUpdate, AvroStationStats> ComputeHourlyStats() {
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
}
