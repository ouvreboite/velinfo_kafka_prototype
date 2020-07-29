package velibstreaming.kafka.stream;

import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Produced;
import velibstreaming.kafka.TopicCreator;
import velibstreaming.kafka.stream.builder.BikesLockedStreamBuilder;
import velibstreaming.kafka.stream.builder.HourlyStationStatsStreamBuilder;
import velibstreaming.kafka.stream.builder.StationUpdatesStreamBuilder;
import velibstreaming.kafka.stream.builder.StationsStatusStreamBuilder;
import velibstreaming.properties.StreamProperties;

import java.util.Properties;
import java.util.concurrent.CountDownLatch;

import static org.apache.kafka.streams.KafkaStreams.State.RUNNING;

public class StreamApplication {

    private KafkaStreams streams;
    private final StreamProperties props = StreamProperties.getInstance();

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
                props.getStationUpdatesTopic(),
                props.getHourlyStationStatsTopic(),
                props.getBikesLockedTopic(),
                props.getStationStatusTopic());

        Topology topology = buildTopology();

        this.streams = new KafkaStreams(topology, buildStreamsProperties());
        this.streams.start();
    }

    private Topology buildTopology() {
        final StreamsBuilder builder = new StreamsBuilder();

        var stationUpdatesStream = new StationUpdatesStreamBuilder().build(builder);
        stationUpdatesStream.to(props.getStationUpdatesTopic(), Produced.with(Serdes.String(), StreamUtils.AvroSerde()));

        var hourlyStationStatsStream = new HourlyStationStatsStreamBuilder().build(stationUpdatesStream);
        hourlyStationStatsStream.to(props.getHourlyStationStatsTopic(), Produced.with(Serdes.String(), StreamUtils.AvroSerde()));

        var bikesLockedStream = new BikesLockedStreamBuilder().build(hourlyStationStatsStream);
        bikesLockedStream.to(props.getBikesLockedTopic(), Produced.with(Serdes.String(), StreamUtils.AvroSerde()));

        var stationsStatusStream = new StationsStatusStreamBuilder().build(stationUpdatesStream);
        stationsStatusStream.to(props.getStationStatusTopic(), Produced.with(Serdes.String(), StreamUtils.AvroSerde()));

        return builder.build();
    }

    public void stop() {
        if(this.streams == null || this.streams.state() != RUNNING )
            throw new IllegalStateException("The stream should be created and running before stopping it");
        this.streams.close();
    }

    private Properties buildStreamsProperties() {
        StreamProperties streamProps = StreamProperties.getInstance();
        final Properties props = new Properties();
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, streamProps.getBootstrapServers());
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "velibstreaming.app");
        props.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, 0);
        props.put(KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG, "true");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        return props;
    }
}