package fr.velinfo.kafka.stream.builder;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.Joined;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import fr.velinfo.avro.record.source.AvroBicycleCount;
import fr.velinfo.avro.record.stream.AvroStationsAffectedByCount;
import fr.velinfo.avro.record.stream.AvroZone;
import fr.velinfo.kafka.stream.StreamUtils;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class StationNearbyCountsStreamBuilder {

    public KStream<String, AvroBicycleCount> build(KStream<String, AvroBicycleCount> countUpdatesProjectedStream, KTable<String, AvroZone> zoneTable) {
        return countUpdatesProjectedStream
                .join(
                        zoneTable,
                        (count, zone) -> new AvroStationsAffectedByCount(new ArrayList<>(zone.getStationsCoordinates().keySet()), count),
                        Joined.with(Serdes.String(), StreamUtils.avroSerde(), StreamUtils.avroSerde())
                )
                .flatMap(
                        (zoneId, stationsAffected) -> stationsAffected.getStationCodes().stream()
                                .map(stationCode -> new KeyValue<>(stationCode, stationsAffected.getCount()))
                                .collect(Collectors.toList())
                );
    }
}
