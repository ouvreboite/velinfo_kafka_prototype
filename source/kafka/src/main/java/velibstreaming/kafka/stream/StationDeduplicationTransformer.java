package velibstreaming.kafka.stream;

import org.apache.kafka.streams.kstream.ValueTransformerWithKey;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.state.KeyValueStore;
import velibstreaming.avro.record.stream.AvroStation;

public class StationDeduplicationTransformer implements ValueTransformerWithKey<String, AvroStation, AvroStation> {

    /**
     * Key: identifier
     * Value: timestamp (event-time) of the corresponding event when the event ID was seen for the
     * first time
     */
    private KeyValueStore<String, String> eventIdStore;
    private final String storeName;


    StationDeduplicationTransformer(final String storeName) {
        this.storeName = storeName;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void init(final ProcessorContext context) {
        this.eventIdStore = (KeyValueStore<String, String>) context.getStateStore(storeName);
    }

    @Override
    public AvroStation transform(final String key, final AvroStation station) {
        final String stationCode = station.getStationCode().toString();
        final String stationState = station.getAvailabilityTimestamp()+"_"+station.getElectricBikesAtStation()+"_"+station.getMechanicalBikesAtStation();

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