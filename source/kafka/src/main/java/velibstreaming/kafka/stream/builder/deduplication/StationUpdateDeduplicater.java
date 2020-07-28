package velibstreaming.kafka.stream.builder.deduplication;

import lombok.Builder;
import org.apache.kafka.streams.kstream.ValueTransformerWithKey;
import org.apache.kafka.streams.processor.ProcessorContext;
import velibstreaming.avro.record.stream.AvroStationUpdate;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

public class StationUpdateDeduplicater implements ValueTransformerWithKey<String, AvroStationUpdate, AvroStationUpdate> {

    @Builder
    private static class DeduplicationState{
        AvroStationUpdate update;
        LocalDateTime timestamp;
    }
    private final Map<String, DeduplicationState> deduplicationCache = new HashMap<>();

    @Override
    public void init(final ProcessorContext context) {
    }

    @Override
    public AvroStationUpdate transform(final String key, final AvroStationUpdate update) {

        var previousState = deduplicationCache.get(key);
        if(previousState == null || !same(previousState.update, update)){
            var newState = new DeduplicationState(update, LocalDateTime.now());
            deduplicationCache.put(key, newState);
            return update;
        }

        if(lessThan15Minutes(previousState.timestamp))
            return null;

        previousState.timestamp = LocalDateTime.now();
        update.setStaleSinceTimestamp(previousState.update.getLoadTimestamp());
        return update;
    }

    private boolean lessThan15Minutes(LocalDateTime timestamp) {
        return timestamp.until(LocalDateTime.now(), ChronoUnit.MINUTES) < 15;
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