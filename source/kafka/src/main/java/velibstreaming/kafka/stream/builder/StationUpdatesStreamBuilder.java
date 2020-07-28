package velibstreaming.kafka.stream.builder;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.ValueMapper;
import velibstreaming.avro.record.source.AvroStationAvailability;
import velibstreaming.avro.record.stream.AvroStationUpdate;
import velibstreaming.kafka.stream.StreamUtils;
import velibstreaming.kafka.stream.builder.deduplication.StationUpdateDeduplicater;
import velibstreaming.properties.StreamProperties;

public class StationUpdatesStreamBuilder {

    public KStream<String, AvroStationUpdate> build(final StreamsBuilder builder) {
        StreamProperties topicProps = StreamProperties.getInstance();

        var availabilityStream = builder.stream(topicProps.getStationAvailabilityTopic(), Consumed.with(Serdes.String(), StreamUtils.<AvroStationAvailability>AvroSerde()));

        return availabilityStream
                .mapValues(mapToStationUpdate())
                .transformValues(StationUpdateDeduplicater::new)
                .filter((k, v) -> v != null);
    }

    private ValueMapper<AvroStationAvailability, AvroStationUpdate> mapToStationUpdate() {
        return (stationAvailability) -> AvroStationUpdate.newBuilder()
                .setStationCode(stationAvailability.getStationCode())
                .setStationName(stationAvailability.getStationName())
                .setStationCapacity(stationAvailability.getStationCapacity())
                .setLatitude(stationAvailability.getLatitude())
                .setLongitude(stationAvailability.getLongitude())
                .setElectricBikesAtStation(stationAvailability.getElectricBikesAtStation())
                .setMechanicalBikesAtStation(stationAvailability.getMechanicalBikesAtStation())
                .setAvailabilityTimestamp(stationAvailability.getAvailabilityTimestamp())
                .setLoadTimestamp(stationAvailability.getLoadTimestamp())
                .setStaleSinceTimestamp(null)
                .setIsInstalled(stationAvailability.getIsInstalled())
                .setIsRenting(stationAvailability.getIsRenting())
                .setIsReturning(stationAvailability.getIsReturning())
                .build();
    }
}
