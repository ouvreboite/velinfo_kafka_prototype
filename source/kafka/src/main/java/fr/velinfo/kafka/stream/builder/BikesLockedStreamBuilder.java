package fr.velinfo.kafka.stream.builder;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.state.WindowStore;
import fr.velinfo.avro.record.stream.AvroBikesLocked;
import fr.velinfo.avro.record.stream.AvroStationStats;
import fr.velinfo.kafka.stream.StreamUtils;

import java.time.Duration;

import static org.apache.kafka.streams.kstream.Suppressed.BufferConfig.unbounded;

public class BikesLockedStreamBuilder {

    public KStream<String, AvroBikesLocked> build(KStream<String, AvroStationStats> hourlyStatsStream){
        TimeWindows twoDaysWindow = TimeWindows
                .of(Duration.ofHours(24))
                .advanceBy(Duration.ofHours(1))
                .grace(Duration.ofMinutes(2));

        var materialized = Materialized.<String, AvroBikesLocked, WindowStore<Bytes, byte[]>>with(Serdes.String(), StreamUtils.avroSerde())
                .withRetention(Duration.ofDays(3));

        return hourlyStatsStream
                .groupByKey(Grouped.with(Serdes.String(), StreamUtils.avroSerde()))
                .windowedBy(twoDaysWindow)
                .aggregate(AvroBikesLocked::new,
                        ComputeBikesLockedEstimation(),
                        materialized)
                .suppress(Suppressed.untilWindowCloses(unbounded()))
                .toStream()
                .map((windowKey, bikesLocked) -> {
                    bikesLocked.setPeriodStart(windowKey.window().start());
                    bikesLocked.setPeriodEnd(windowKey.window().end());
                    return new KeyValue<>(windowKey.key(), bikesLocked);
                });
    }

    private Aggregator<String, AvroStationStats, AvroBikesLocked> ComputeBikesLockedEstimation() {
        return (key, stats, bikesLocked) -> {
            bikesLocked.setStationCode(stats.getStationCode());

            if(bikesLocked.getElectricBikesLocked() == null){
                bikesLocked.setElectricBikesLocked(stats.getMinimumNumberOfElectricBikes());
                bikesLocked.setMechanicalBikesLocked(stats.getMinimumNumberOfMechanicalBikes());
                bikesLocked.setEmptySlotsLocked(stats.getMinimumNumberOfEmptySlots());
            }else{
                bikesLocked.setMechanicalBikesLocked(Math.min(bikesLocked.getMechanicalBikesLocked(), stats.getMinimumNumberOfMechanicalBikes()));
                bikesLocked.setElectricBikesLocked(Math.min(bikesLocked.getElectricBikesLocked(), stats.getMinimumNumberOfElectricBikes()));
                bikesLocked.setEmptySlotsLocked(Math.min(bikesLocked.getEmptySlotsLocked(), stats.getMinimumNumberOfEmptySlots()));
            }
            return bikesLocked;
        };
    }
}
