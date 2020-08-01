package velibstreaming.kafka.stream.builder;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.state.WindowStore;
import velibstreaming.avro.record.source.AvroBicycleCount;
import velibstreaming.avro.record.stream.AvroNearbyTraffic;
import velibstreaming.kafka.stream.StreamUtils;

import java.time.Duration;

import static org.apache.kafka.streams.kstream.Suppressed.BufferConfig.unbounded;

public class HourlyStationNearbyTrafficStreamBuilder {

    public KStream<String, AvroNearbyTraffic> build(KStream<String, AvroBicycleCount> stationNearbyCountsStream){
        TimeWindows hourWindow = TimeWindows
                .of(Duration.ofHours(1))
                .grace(Duration.ofHours(36));

        var materializedHour = Materialized.<String, AvroNearbyTraffic, WindowStore<Bytes, byte[]>>with(Serdes.String(), StreamUtils.AvroSerde())
                .withRetention(Duration.ofDays(2));

        return stationNearbyCountsStream
                .groupByKey(Grouped.with(Serdes.String(), StreamUtils.AvroSerde()))
                .windowedBy(hourWindow)
                .aggregate(() -> AvroNearbyTraffic.newBuilder().build(),
                        ComputeTraffic(),
                        materializedHour)
                .suppress(Suppressed.untilWindowCloses(unbounded()))
                .toStream()
                .map((windowKey, stationStats) -> {
                    stationStats.setPeriodStart(windowKey.window().start());
                    stationStats.setPeriodEnd(windowKey.window().end());
                    return new KeyValue<>(windowKey.key(), stationStats);
                });
    }

    private Aggregator<String, AvroBicycleCount, AvroNearbyTraffic> ComputeTraffic() {
        return (stationCode, count, traffic) -> {
            traffic.getCounts().add(count);
            traffic.setTotalTraffic(traffic.getTotalTraffic()+count.getCount());
            return traffic;
        };
    }
}
