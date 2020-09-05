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
        TimeWindows hourWindow = TimeWindows
                .of(Duration.ofHours(1))
                .grace(Duration.ofMinutes(1));

        var materializedHour = Materialized.<String, AvroStationStats, WindowStore<Bytes, byte[]>>with(Serdes.String(), StreamUtils.avroSerde())
                .withRetention(Duration.ofHours(6));

        return stationChangesStream
                .groupByKey(Grouped.with(Serdes.String(), StreamUtils.avroSerde()))
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
            stats.setStationCode(stationChange.getStationCode());

            computeMinima(stationChange, stats);
            computeTotal(stationChange, stats);

            stats.setLastNumberOfElectricBikes(stationChange.getElectricBikesAtStation());
            stats.setLastNumberOfMechanicalBikes(stationChange.getMechanicalBikesAtStation());
            stats.setLastLoadTimestamp(stationChange.getLoadTimestamp());
            return stats;
        };
    }

    private void computeTotal(AvroStationUpdate update, AvroStationStats stats) {
        stats.setNumberOfMechanicalBikesRented(stats.getNumberOfMechanicalBikesRented()+update.getMechanicalBikesRented());
        stats.setNumberOfMechanicalBikesReturned(stats.getNumberOfMechanicalBikesReturned()+update.getMechanicalBikesReturned());

        stats.setNumberOfElectricBikesRented(stats.getNumberOfElectricBikesRented()+update.getElectricBikesRented());
        stats.setNumberOfElectricBikesReturned(stats.getNumberOfElectricBikesReturned()+update.getElectricBikesReturned());
    }

    private void computeMinima(AvroStationUpdate stationChange, AvroStationStats stats) {
        if(stats.getMinimumNumberOfElectricBikes() == null){
            stats.setMinimumNumberOfMechanicalBikes(stationChange.getMechanicalBikesAtStation());
            stats.setMinimumNumberOfElectricBikes(stationChange.getElectricBikesAtStation());
            int emptySlots = stationChange.getStationCapacity() - stationChange.getElectricBikesAtStation() - stationChange.getMechanicalBikesAtStation();
            stats.setMinimumNumberOfEmptySlots(emptySlots);
        }else{
            int minimumMechanicalBikes = Math.min(stationChange.getMechanicalBikesAtStation(), stats.getMinimumNumberOfMechanicalBikes());
            stats.setMinimumNumberOfMechanicalBikes(minimumMechanicalBikes);
            int minimumElectricalBikes = Math.min(stationChange.getElectricBikesAtStation(), stats.getMinimumNumberOfElectricBikes());
            stats.setMinimumNumberOfElectricBikes(minimumElectricalBikes);
            int emptySlots = stationChange.getStationCapacity() - stationChange.getElectricBikesAtStation() - stationChange.getMechanicalBikesAtStation();
            int minimumEmptySlots = Math.min(emptySlots, stats.getMinimumNumberOfEmptySlots());
            stats.setMinimumNumberOfEmptySlots(minimumEmptySlots);
        }
    }
}
