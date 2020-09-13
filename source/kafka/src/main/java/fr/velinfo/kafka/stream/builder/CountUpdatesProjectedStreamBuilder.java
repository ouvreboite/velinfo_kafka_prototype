package fr.velinfo.kafka.stream.builder;

import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KeyValueMapper;
import fr.velinfo.avro.record.source.AvroBicycleCount;
import fr.velinfo.kafka.stream.builder.projection.GeoProjector;

import java.util.List;
import java.util.stream.Collectors;

public class CountUpdatesProjectedStreamBuilder {
    private final GeoProjector projector = new GeoProjector();

    public KStream<String, AvroBicycleCount> build(KStream<String, AvroBicycleCount> countUpdatesStream) {
        return countUpdatesStream
                .flatMap(projectOnNearbyZones());
    }

    private KeyValueMapper<String, AvroBicycleCount, Iterable<KeyValue<String, AvroBicycleCount>>> projectOnNearbyZones() {
        return (countId, count) -> {
            List<String> closeByZones = projector.get100MetersNearbyZones(count.getCoordinates());

            return closeByZones.stream()
                    .map(zone -> new KeyValue<>(zone, count))
                    .collect(Collectors.toList());
        };
    }
}
