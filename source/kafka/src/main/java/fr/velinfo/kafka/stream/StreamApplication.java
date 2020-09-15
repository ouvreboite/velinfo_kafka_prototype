package fr.velinfo.kafka.stream;

import fr.velinfo.avro.record.source.AvroStationAvailability;
import fr.velinfo.kafka.TopicCreator;
import fr.velinfo.kafka.stream.builder.BikesLockedStreamBuilder;
import fr.velinfo.kafka.stream.builder.HourlyStationStatsStreamBuilder;
import fr.velinfo.kafka.stream.builder.StationUpdatesStreamBuilder;
import fr.velinfo.kafka.stream.builder.StationsStatusStreamBuilder;
import fr.velinfo.properties.ConnectionConfiguration;
import fr.velinfo.properties.Topics;
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Produced;

import java.util.Properties;
import java.util.concurrent.CountDownLatch;

import static org.apache.kafka.streams.KafkaStreams.State.RUNNING;

public class StreamApplication {

    private KafkaStreams streams;

    public static void main(final String[] args) {
        var app = new StreamApplication();

        final CountDownLatch latch = new CountDownLatch(1);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            app.stop();
            latch.countDown();
        }));

        try {
            app.start();
            latch.await();
        } catch (InterruptedException ignored) {
        }
    }

    public void start() {
        TopicCreator.createTopicIfNeeded(
                Topics.STATION_UPDATES,
                Topics.HOURLY_STATION_STATS,
                Topics.BIKES_LOCKED,
                Topics.STATION_STATUS
        );

        Topology topology = buildTopology();

        this.streams = new KafkaStreams(topology, buildStreamsProperties());
        this.streams.cleanUp();
        this.streams.start();
    }

    private Topology buildTopology() {
        final StreamsBuilder builder = new StreamsBuilder();
        var availabilityStream = builder.stream(Topics.STATION_AVAILABILITIES, Consumed.with(Serdes.String(), StreamUtils.<AvroStationAvailability>avroSerde()));

        var stationUpdatesStream = new StationUpdatesStreamBuilder().build(builder, availabilityStream);
        stationUpdatesStream.to(Topics.STATION_UPDATES, Produced.with(Serdes.String(), StreamUtils.avroSerde()));

        var hourlyStationStatsStream = new HourlyStationStatsStreamBuilder().build(stationUpdatesStream);
        hourlyStationStatsStream.to(Topics.HOURLY_STATION_STATS, Produced.with(Serdes.String(), StreamUtils.avroSerde()));

        var bikesLockedStream = new BikesLockedStreamBuilder().build(hourlyStationStatsStream);
        bikesLockedStream.to(Topics.BIKES_LOCKED, Produced.with(Serdes.String(), StreamUtils.avroSerde()));

        var stationsStatusStream = new StationsStatusStreamBuilder().build(stationUpdatesStream);
        stationsStatusStream.to(Topics.STATION_STATUS, Produced.with(Serdes.String(), StreamUtils.avroSerde()));

        return builder.build();
    }

    public void stop() {
        if(this.streams == null || this.streams.state() != RUNNING )
            throw new IllegalStateException("The stream should be created and running before stopping it");
        this.streams.close();
    }

    private Properties buildStreamsProperties() {
        final Properties props = new Properties();
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, ConnectionConfiguration.getInstance().getBootstrapServers());
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "velinfo.app");
        props.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, 0);
        props.put(KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG, "true");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        return props;
    }
}