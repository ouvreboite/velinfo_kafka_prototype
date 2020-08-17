package velibstreaming.kafka.stream.builder.deduplication;

import org.apache.kafka.streams.kstream.ValueTransformerWithKey;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.state.KeyValueStore;
import velibstreaming.avro.record.stream.AvroStationUpdate;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

public class StationUpdateDeduplicater implements ValueTransformerWithKey<String, AvroStationUpdate, AvroStationUpdate> {

    private KeyValueStore<String, AvroStationUpdate> deduplicationStore;
    private final String storeName;

    public StationUpdateDeduplicater(final String storeName) {
        this.storeName = storeName;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void init(final ProcessorContext context) {
        this.deduplicationStore = (KeyValueStore<String, AvroStationUpdate>) context.getStateStore(storeName);
    }


    @Override
    public AvroStationUpdate transform(final String stationCode, final AvroStationUpdate update) {

        var previous = deduplicationStore.get(stationCode);
        if(previous == null || !same(previous, update)){
            deduplicationStore.put(stationCode, update);
            return update;
        }

        if(lessThan15MinutesDiff(previous.getLoadTimestamp(), update.getLoadTimestamp()))
            return null;

        long staleSince = previous.getStaleSinceTimestamp() == null ?
                previous.getLoadTimestamp() :
                previous.getStaleSinceTimestamp();
        update.setStaleSinceTimestamp(staleSince);

        deduplicationStore.put(stationCode, update);
        return update;
    }

    private boolean lessThan15MinutesDiff(long beforeTimestampmillis, long afterTimestampmillis) {
        LocalDateTime before = LocalDateTime.ofInstant(Instant.ofEpochMilli(beforeTimestampmillis), ZoneId.systemDefault());
        LocalDateTime after = LocalDateTime.ofInstant(Instant.ofEpochMilli(afterTimestampmillis), ZoneId.systemDefault());
        return before.until(after, ChronoUnit.MINUTES) < 15;
    }

    private boolean same(AvroStationUpdate update1, AvroStationUpdate update2) {
        return update1.getElectricBikesAtStation() == update2.getElectricBikesAtStation()
                && update1.getMechanicalBikesAtStation() == update2.getMechanicalBikesAtStation()
                && update1.getIsRenting() == update2.getIsRenting()
                && update1.getIsReturning() == update2.getIsReturning()
                && update1.getIsInstalled() == update2.getIsInstalled();
    }

    @Override
    public void close() {
        // Note: The store should NOT be closed manually here via `eventIdStore.close()`!
        // The Kafka Streams API will automatically close stores when necessary.
    }

}