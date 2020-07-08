package velibstreaming.kafka.stream.builder;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.ValueMapper;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.StoreBuilder;
import org.apache.kafka.streams.state.Stores;
import velibstreaming.avro.record.source.AvroStationAvailability;
import velibstreaming.avro.record.stream.AvroStationChange;
import velibstreaming.kafka.stream.StreamUtils;
import velibstreaming.properties.StreamProperties;

public class StationChangesStreamBuilder {

    public KStream<String, AvroStationChange> build(final StreamsBuilder builder) {
        StreamProperties topicProps = StreamProperties.getInstance();

        final String deduplicationStore = "stationDeduplicationStore";
        final StoreBuilder<KeyValueStore<String, String>> dedupStoreBuilder = Stores.keyValueStoreBuilder(
                Stores.persistentKeyValueStore(deduplicationStore),
                Serdes.String(),
                Serdes.String()
        );
        builder.addStateStore(dedupStoreBuilder);

        var availabilityStream = builder.stream(topicProps.getStationAvailabilityTopic(), Consumed.with(Serdes.String(), StreamUtils.<AvroStationAvailability>AvroSerde()));

        return availabilityStream
                .mapValues(mapToStationChange())
                .transformValues(() -> new StationDeduplicationTransformer(deduplicationStore), deduplicationStore)
                .filter((k, v) -> v != null);

    }

    private ValueMapper<AvroStationAvailability, AvroStationChange> mapToStationChange() {
        return (stationAvailability) -> AvroStationChange.newBuilder()
                .setStationCode(stationAvailability.getStationCode())
                .setStationName(stationAvailability.getStationName())
                .setStationCapacity(stationAvailability.getStationCapacity())
                .setLatitude(stationAvailability.getLatitude())
                .setLongitude(stationAvailability.getLongitude())
                .setElectricBikesAtStation(stationAvailability.getElectricBikesAtStation())
                .setMechanicalBikesAtStation(stationAvailability.getMechanicalBikesAtStation())
                .setAvailabilityTimestamp(stationAvailability.getAvailabilityTimestamp())
                .setLoadTimestamp(stationAvailability.getLoadTimestamp())
                .setStaleSinceTimestamp(null)
                .setIsInstalled(stationAvailability.getIsInstalled())
                .setIsRenting(stationAvailability.getIsRenting())
                .setIsReturning(stationAvailability.getIsReturning())
                .build();
    }
}
