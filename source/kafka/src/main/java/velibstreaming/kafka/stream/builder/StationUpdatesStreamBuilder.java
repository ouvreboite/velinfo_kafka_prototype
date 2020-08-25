package velibstreaming.kafka.stream.builder;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.ValueMapper;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.StoreBuilder;
import org.apache.kafka.streams.state.Stores;
import velibstreaming.avro.record.source.AvroStationAvailability;
import velibstreaming.avro.record.stream.AvroStationUpdate;
import velibstreaming.kafka.stream.StreamUtils;
import velibstreaming.kafka.stream.builder.deduplication.StationUpdateDeduplicater;

public class StationUpdatesStreamBuilder {

    public KStream<String, AvroStationUpdate> build(final StreamsBuilder builder, final KStream<String, AvroStationAvailability> availabilityStream) {
        final String deduplicationStore = "stationDeduplicationStore";
        final StoreBuilder<KeyValueStore<String, AvroStationUpdate>> deduplicationStoreBuilder = Stores.keyValueStoreBuilder(
                Stores.persistentKeyValueStore(deduplicationStore),
                Serdes.String(),
                StreamUtils.AvroSerde()
        );
        builder.addStateStore(deduplicationStoreBuilder);

        return availabilityStream
                .mapValues(mapToStationUpdate())
                .transformValues(() -> new StationUpdateDeduplicater(deduplicationStore), deduplicationStore)
                .filter((k, v) -> v != null);
    }

    private ValueMapper<AvroStationAvailability, AvroStationUpdate> mapToStationUpdate() {
        return (stationAvailability) -> AvroStationUpdate.newBuilder()
                .setStationCode(stationAvailability.getStationCode())
                .setStationName(stationAvailability.getStationName())
                .setStationCapacity(stationAvailability.getStationCapacity())
                .setCoordinates(stationAvailability.getCoordinates())
                .setElectricBikesAtStation(stationAvailability.getElectricBikesAtStation())
                .setMechanicalBikesAtStation(stationAvailability.getMechanicalBikesAtStation())
                .setElectricBikesRented(0)
                .setElectricBikesReturned(0)
                .setMechanicalBikesRented(0)
                .setMechanicalBikesReturned(0)
                .setAvailabilityTimestamp(stationAvailability.getAvailabilityTimestamp())
                .setLoadTimestamp(stationAvailability.getLoadTimestamp())
                .setLastMovementTimestamp(null)
                .setIsInstalled(stationAvailability.getIsInstalled())
                .setIsRenting(stationAvailability.getIsRenting())
                .setIsReturning(stationAvailability.getIsReturning())
                .build();
    }
}
