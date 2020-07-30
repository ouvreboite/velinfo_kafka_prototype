package velibstreaming.kafka.stream.builder;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.state.StoreBuilder;
import org.apache.kafka.streams.state.Stores;
import org.apache.kafka.streams.state.WindowStore;
import velibstreaming.avro.record.source.AvroBicycleCount;
import velibstreaming.kafka.stream.StreamUtils;
import velibstreaming.kafka.stream.builder.deduplication.CountUpdateDeduplicater;

import java.time.Duration;

public class CountUpdatesStreamBuilder {

    public KStream<String, AvroBicycleCount> build(final StreamsBuilder builder, final KStream<String, AvroBicycleCount> countsStream) {

        countsStream.toTable();
        final String deduplicationStore = "countDeduplicationStore";
        final StoreBuilder<WindowStore<String, AvroBicycleCount>> deduplicationStoreBuilder = Stores.windowStoreBuilder(
                Stores.persistentWindowStore(deduplicationStore, Duration.ofDays(2), Duration.ofDays(2), false),
                Serdes.String(),
                StreamUtils.AvroSerde()
        );
        builder.addStateStore(deduplicationStoreBuilder);

        return countsStream
                .transformValues(() -> new CountUpdateDeduplicater(deduplicationStore), deduplicationStore)
                .filter((k, v) -> v != null);
    }
}
