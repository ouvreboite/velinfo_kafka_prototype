package fr.velinfo.kafka.stream.builder;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.ValueMapper;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.StoreBuilder;
import org.apache.kafka.streams.state.Stores;
import fr.velinfo.avro.record.source.AvroStationAvailability;
import fr.velinfo.avro.record.stream.AvroStationUpdate;
import fr.velinfo.kafka.stream.AvroSerdeBuilder;
import fr.velinfo.kafka.stream.builder.deduplication.StationUpdateDeduplicator;
import org.springframework.stereotype.Component;

@Component
public class StationUpdatesStreamBuilder {

    private final AvroSerdeBuilder avroSerdeBuilder;

    public StationUpdatesStreamBuilder(AvroSerdeBuilder avroSerdeBuilder) {
        this.avroSerdeBuilder = avroSerdeBuilder;
    }

    public KStream<String, AvroStationUpdate> build(final StreamsBuilder builder, final KStream<String, AvroStationAvailability> availabilityStream) {
        final String deduplicationStore = "stationDeduplicationStore";
        final StoreBuilder<KeyValueStore<String, AvroStationUpdate>> deduplicationStoreBuilder = Stores.keyValueStoreBuilder(
                Stores.persistentKeyValueStore(deduplicationStore),
                Serdes.String(),
                avroSerdeBuilder.avroSerde()
        );
        builder.addStateStore(deduplicationStoreBuilder);

        return availabilityStream
                .mapValues(mapToStationUpdate())
                .transformValues(() -> new StationUpdateDeduplicator(deduplicationStore), deduplicationStore)
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
                .setLastChangeTimestamp(null)
                .setIsInstalled(stationAvailability.getIsInstalled())
                .setIsRenting(stationAvailability.getIsRenting())
                .setIsReturning(stationAvailability.getIsReturning())
                .build();
    }
}
