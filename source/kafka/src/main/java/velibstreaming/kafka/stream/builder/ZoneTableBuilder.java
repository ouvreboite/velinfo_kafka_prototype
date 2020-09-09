package velibstreaming.kafka.stream.builder;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.*;
import velibstreaming.avro.record.stream.AvroStationUpdate;
import velibstreaming.avro.record.stream.AvroZone;
import velibstreaming.kafka.stream.StreamUtils;
import velibstreaming.kafka.stream.builder.projection.GeoProjector;

public class ZoneTableBuilder {
    private final GeoProjector projector = new GeoProjector();

    public KTable<String, AvroZone> build(KStream<String, AvroStationUpdate> stationUpdatesStream) {
        KTable<String, AvroZone> zones = stationUpdatesStream
                .map(projectOnZone())
                .groupByKey(Grouped.with(Serdes.String(), StreamUtils.avroSerde()))
                .aggregate(
                        () -> AvroZone.newBuilder().build(),
                        (zoneId, station, zone) -> {
                            zone.setZoneId(zoneId);
                            zone.getStationsCoordinates().put(station.getStationCode(), station.getCoordinates());
                            return zone;
                        },
                        Materialized.with(Serdes.String(), StreamUtils.avroSerde())
                );
        return zones;
    }

    private KeyValueMapper<String, AvroStationUpdate, KeyValue<String, AvroStationUpdate>> projectOnZone() {
        return (key, station) -> {
            String stationZone = projector.get100MeterZone(station.getCoordinates());
            return new KeyValue<>(stationZone, station);
        };
    }
}
