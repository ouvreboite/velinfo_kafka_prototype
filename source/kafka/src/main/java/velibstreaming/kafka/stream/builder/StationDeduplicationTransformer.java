package velibstreaming.kafka.stream.builder;

import org.apache.kafka.streams.kstream.ValueTransformerWithKey;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.state.KeyValueStore;
import velibstreaming.avro.record.stream.AvroStationChange;

public class StationDeduplicationTransformer implements ValueTransformerWithKey<String, AvroStationChange, AvroStationChange> {

    private KeyValueStore<String, String> eventIdStore;
    private final String storeName;

    public StationDeduplicationTransformer(final String storeName) {
        this.storeName = storeName;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void init(final ProcessorContext context) {
        this.eventIdStore = (KeyValueStore<String, String>) context.getStateStore(storeName);
    }

    @Override
    public AvroStationChange transform(final String key, final AvroStationChange station) {
        final String stationCode = station.getStationCode();
        final String stationState = station.getAvailabilityTimestamp()
                +"_"+station.getElectricBikesAtStation()
                +"_"+station.getMechanicalBikesAtStation()
                +"_"+station.getIsRenting()
                +"_"+station.getIsReturning();

        String previousState = eventIdStore.get(stationCode);
        eventIdStore.put(stationCode, stationState);

        if (previousState != null && previousState.equals(stationState)) {
            return null;
        }
        return station;
    }

    @Override
    public void close() {
        // Note: The store should NOT be closed manually here via `eventIdStore.close()`!
        // The Kafka Streams API will automatically close stores when necessary.
    }

}