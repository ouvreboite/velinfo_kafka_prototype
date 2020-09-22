package fr.velinfo.kafka.stream;

import fr.velinfo.avro.record.source.AvroStationAvailability;
import fr.velinfo.kafka.stream.builder.BikesLockedStreamBuilder;
import fr.velinfo.kafka.stream.builder.HourlyStationStatsStreamBuilder;
import fr.velinfo.kafka.stream.builder.StationUpdatesStreamBuilder;
import fr.velinfo.kafka.stream.builder.StationsStatusStreamBuilder;
import fr.velinfo.common.Topics;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Produced;
import org.springframework.stereotype.Component;
@Component
public class TopologyBuilder {

    private final AvroSerdeBuilder serdeBuilder;
    private final StationUpdatesStreamBuilder stationUpdatesStreamBuilder;
    private final HourlyStationStatsStreamBuilder hourlyStationStatsStreamBuilder;
    private final BikesLockedStreamBuilder bikesLockedStreamBuilder;
    private final StationsStatusStreamBuilder stationsStatusStreamBuilder;

    public TopologyBuilder(AvroSerdeBuilder serdeBuilder, StationUpdatesStreamBuilder stationUpdatesStreamBuilder, HourlyStationStatsStreamBuilder hourlyStationStatsStreamBuilder, BikesLockedStreamBuilder bikesLockedStreamBuilder, StationsStatusStreamBuilder stationsStatusStreamBuilder) {
        this.serdeBuilder = serdeBuilder;
        this.stationUpdatesStreamBuilder = stationUpdatesStreamBuilder;
        this.hourlyStationStatsStreamBuilder = hourlyStationStatsStreamBuilder;
        this.bikesLockedStreamBuilder = bikesLockedStreamBuilder;
        this.stationsStatusStreamBuilder = stationsStatusStreamBuilder;
    }

    public Topology buildTopology() {
        final StreamsBuilder builder = new StreamsBuilder();
        var availabilityStream = builder.stream(Topics.STATION_AVAILABILITIES, Consumed.with(Serdes.String(), serdeBuilder.<AvroStationAvailability>avroSerde()));

        var stationUpdatesStream = stationUpdatesStreamBuilder.build(builder, availabilityStream);
        stationUpdatesStream.to(Topics.STATION_UPDATES, Produced.with(Serdes.String(), serdeBuilder.avroSerde()));

        var hourlyStationStatsStream = hourlyStationStatsStreamBuilder.build(stationUpdatesStream);
        hourlyStationStatsStream.to(Topics.HOURLY_STATION_STATS, Produced.with(Serdes.String(), serdeBuilder.avroSerde()));

        var bikesLockedStream = bikesLockedStreamBuilder.build(hourlyStationStatsStream);
        bikesLockedStream.to(Topics.BIKES_LOCKED, Produced.with(Serdes.String(), serdeBuilder.avroSerde()));

        var stationsStatusStream = stationsStatusStreamBuilder.build(stationUpdatesStream);
        stationsStatusStream.to(Topics.STATION_STATUS, Produced.with(Serdes.String(), serdeBuilder.avroSerde()));

        return builder.build();
    }
}