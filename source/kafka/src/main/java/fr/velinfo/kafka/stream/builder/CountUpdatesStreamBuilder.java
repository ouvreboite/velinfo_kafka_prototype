package fr.velinfo.kafka.stream.builder;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.StoreBuilder;
import org.apache.kafka.streams.state.Stores;
import fr.velinfo.avro.record.source.AvroBicycleCount;
import fr.velinfo.kafka.stream.StreamUtils;
import fr.velinfo.kafka.stream.builder.deduplication.CountUpdateDeduplicator;

public class CountUpdatesStreamBuilder {

    public KStream<String, AvroBicycleCount> build(final StreamsBuilder builder, final KStream<String, AvroBicycleCount> countsStream) {
        final String deduplicationStore = "countDeduplicationStore";
        final StoreBuilder<KeyValueStore<String, AvroBicycleCount>> deduplicationStoreBuilder = Stores.keyValueStoreBuilder(
                Stores.persistentKeyValueStore(deduplicationStore),
                Serdes.String(),
                StreamUtils.avroSerde()
        );
        builder.addStateStore(deduplicationStoreBuilder);

        return countsStream
                .transformValues(() -> new CountUpdateDeduplicator(deduplicationStore), deduplicationStore)
                .filter((k, v) -> v != null);
    }
}
