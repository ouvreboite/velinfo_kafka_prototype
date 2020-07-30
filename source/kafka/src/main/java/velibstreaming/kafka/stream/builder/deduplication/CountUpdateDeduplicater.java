package velibstreaming.kafka.stream.builder.deduplication;

import org.apache.kafka.streams.kstream.ValueTransformerWithKey;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.state.WindowStore;
import velibstreaming.avro.record.source.AvroBicycleCount;

public class CountUpdateDeduplicater implements ValueTransformerWithKey<String, AvroBicycleCount, AvroBicycleCount> {

    private WindowStore<String, AvroBicycleCount> deduplicationStore;
    private final String storeName;

    public CountUpdateDeduplicater(final String storeName) {
        this.storeName = storeName;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void init(final ProcessorContext context) {
        this.deduplicationStore = (WindowStore<String, AvroBicycleCount>) context.getStateStore(storeName);
    }


    @Override
    public AvroBicycleCount transform(final String counterId, final AvroBicycleCount update) {
        String key = counterId+"_"+update.getCountTimestamp();
        AvroBicycleCount previous = deduplicationStore.fetch(key, update.getCountTimestamp());
        if(previous != null)
            return null;

        deduplicationStore.put(key, update, update.getCountTimestamp());
        return update;
    }

    @Override
    public void close() {
        // Note: The store should NOT be closed manually here via `eventIdStore.close()`!
        // The Kafka Streams API will automatically close stores when necessary.
    }

}