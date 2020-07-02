package velibstreaming.kafka.stream;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.ValueJoiner;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.StoreBuilder;
import org.apache.kafka.streams.state.Stores;
import velibstreaming.avro.record.source.AvroRealTimeAvailability;
import velibstreaming.avro.record.source.AvroStationCharacteristics;
import velibstreaming.avro.record.stream.AvroStation;
import velibstreaming.properties.StreamProperties;

public class StationChangesStreamBuilder {

    public KStream<String, AvroStation> build(final StreamsBuilder builder) {
        StreamProperties topicProps = StreamProperties.getInstance();

        final String deduplicationStore = "stationDeduplicationStore";
        final StoreBuilder<KeyValueStore<String, String>> dedupStoreBuilder = Stores.keyValueStoreBuilder(
                Stores.persistentKeyValueStore(deduplicationStore),
                Serdes.String(),
                Serdes.String()
        );
        builder.addStateStore(dedupStoreBuilder);

        var availabilityStream = builder.stream(topicProps.getAvailabilityTopic(), Consumed.with(Serdes.String(), StreamUtils.<AvroRealTimeAvailability>AvroSerde()));
        var characteristicsTable = builder.table(topicProps.getStationsCharacteristicsTopic(), Consumed.with(Serdes.String(),StreamUtils.<AvroStationCharacteristics>AvroSerde()));

        return availabilityStream
                .join(characteristicsTable, CreateStation())
                .transformValues(() -> new StationDeduplicationTransformer(deduplicationStore), deduplicationStore)
                .filter((k, v) -> v != null);

    }

    private ValueJoiner<AvroRealTimeAvailability, AvroStationCharacteristics, AvroStation> CreateStation() {
        return (a, c) -> AvroStation.newBuilder()
                .setStationCode(c.getStationCode())
                .setStationName(c.getStationName())
                .setTotalCapacity(c.getTotalCapacity())
                .setLatitude(c.getLatitude())
                .setLongitude(c.getLongitude())
                .setElectricBikesAtStation(a.getElectricBikesAtStation())
                .setMechanicalBikesAtStation(a.getMechanicalBikesAtStation())
                .setAvailabilityTimestamp(a.getAvailabilityTimestamp())
                .setLoadTimestamp(a.getLoadTimestamp())
                .setStaleSinceTimestamp(null)
                .build();
    }
}
