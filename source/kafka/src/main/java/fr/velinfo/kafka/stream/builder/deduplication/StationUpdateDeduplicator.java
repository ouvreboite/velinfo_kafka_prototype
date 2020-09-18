package fr.velinfo.kafka.stream.builder.deduplication;

import org.apache.kafka.streams.kstream.ValueTransformerWithKey;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.state.KeyValueStore;
import fr.velinfo.avro.record.stream.AvroStationUpdate;
import fr.velinfo.kafka.utils.DateTimeUtils;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class StationUpdateDeduplicator implements ValueTransformerWithKey<String, AvroStationUpdate, AvroStationUpdate> {

    private KeyValueStore<String, AvroStationUpdate> deduplicationStore;
    private final String storeName;

    public StationUpdateDeduplicator(final String storeName) {
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
        if(previous == null){
            deduplicationStore.put(stationCode, update);
            return update;
        }

        if(same(previous, update)){
            if(lessThan15MinutesDiff(previous.getLoadTimestamp(), update.getLoadTimestamp()))
                return null;
            else
                computeLastMovement(update, previous);
        }
        computeDiffs(update, previous);
        deduplicationStore.put(stationCode, update);
        return update;
    }

    private void computeDiffs(AvroStationUpdate newUpdate, AvroStationUpdate previous) {
        int mechanicalDiff = newUpdate.getMechanicalBikesAtStation() - previous.getMechanicalBikesAtStation();
        newUpdate.setMechanicalBikesRented(mechanicalDiff < 0 ? -mechanicalDiff : 0);
        newUpdate.setMechanicalBikesReturned(mechanicalDiff > 0 ? mechanicalDiff : 0);

        int electricDiff = newUpdate.getElectricBikesAtStation() - previous.getElectricBikesAtStation();
        newUpdate.setElectricBikesRented(electricDiff < 0 ? -electricDiff : 0);
        newUpdate.setElectricBikesReturned(electricDiff > 0 ? electricDiff : 0);
    }

    private void computeLastMovement(AvroStationUpdate newUpdate, AvroStationUpdate previous) {
        long lastMovement = previous.getLastChangeTimestamp() == null ?
                previous.getLoadTimestamp() :
                previous.getLastChangeTimestamp();
        newUpdate.setLastChangeTimestamp(lastMovement);
    }

    private boolean lessThan15MinutesDiff(long beforeTimestampmillis, long afterTimestampmillis) {
        LocalDateTime before = DateTimeUtils.localDateTime(beforeTimestampmillis);
        LocalDateTime after = DateTimeUtils.localDateTime(afterTimestampmillis);
        return before.until(after, ChronoUnit.MINUTES) < 15;
    }

    private boolean same(AvroStationUpdate update1, AvroStationUpdate update2) {
        if(update1 == null || update2 == null)
            return false;
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