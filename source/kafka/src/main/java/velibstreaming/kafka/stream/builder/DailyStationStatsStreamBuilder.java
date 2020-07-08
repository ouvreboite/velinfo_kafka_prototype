package velibstreaming.kafka.stream.builder;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.state.WindowStore;
import velibstreaming.avro.record.stream.AvroDailyStationStats;
import velibstreaming.avro.record.stream.AvroStationChange;
import velibstreaming.kafka.stream.StreamUtils;

import java.time.Duration;

import static org.apache.kafka.streams.kstream.Suppressed.BufferConfig.unbounded;

public class DailyStationStatsStreamBuilder {

    public KStream<String, AvroDailyStationStats> build(KStream<String, AvroStationChange> stationChangesStream){
        TimeWindows dailyWindow = TimeWindows.of(Duration.ofDays(1)).grace(Duration.ofMinutes(5));

        var materialized = Materialized.<String, AvroDailyStationStats, WindowStore<Bytes, byte[]>>with(Serdes.String(), StreamUtils.AvroSerde())
                .withRetention(Duration.ofDays(2));

        return stationChangesStream
                .groupByKey(Grouped.with(Serdes.String(), StreamUtils.AvroSerde()))
                .windowedBy(dailyWindow)
                .aggregate(AvroDailyStationStats::new,
                        ComputeStats(),
                        materialized)
                .suppress(Suppressed.untilWindowCloses(unbounded()))
                .toStream()
                .map(enrichStatsWithWindowStartEnd());
    }

    private KeyValueMapper<Windowed<String>, AvroDailyStationStats, KeyValue<? extends String, ? extends AvroDailyStationStats>> enrichStatsWithWindowStartEnd() {
        return (windowKey, avroDailyStationStats) -> {
            avroDailyStationStats.setPeriodStart(windowKey.window().start());
            avroDailyStationStats.setPeriodEnd(windowKey.window().end());
            return new KeyValue<>(windowKey.key(), avroDailyStationStats);
        };
    }

    private Aggregator<String, AvroStationChange, AvroDailyStationStats> ComputeStats() {
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
