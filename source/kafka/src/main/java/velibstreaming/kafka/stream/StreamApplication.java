package velibstreaming.kafka.stream;

import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.StoreBuilder;
import org.apache.kafka.streams.state.Stores;
import velibstreaming.avro.record.source.AvroRealTimeAvailability;
import velibstreaming.avro.record.source.AvroStationCharacteristics;
import velibstreaming.avro.record.stream.AvroStation;
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
        Topology topology = buildTopology();

        this.streams = new KafkaStreams(topology, buildStreamsProperties());
        this.streams.start();
    }

    private Topology buildTopology() {
        final StreamsBuilder builder = new StreamsBuilder();

        var stationChangesStream = buildStationChangesStream(builder);
        stationChangesStream.to(props.getStationChangesTopic(), Produced.with(Serdes.String(), StreamUtils.AvroSerde()));

        var stationsWithStaleTimestampStream = buildStationsWithStaleTimestampStream(stationChangesStream);
        stationsWithStaleTimestampStream.to(props.getStationChangesWithStaleTimestampTopic(), Produced.with(Serdes.String(),StreamUtils.AvroSerde()));

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

    private KStream<String, AvroStation> buildStationChangesStream(final StreamsBuilder builder) {
        StreamProperties topicProps = StreamProperties.getInstance();

        final String deduplicationStore = "stationDeduplicationStore";
        final StoreBuilder<KeyValueStore<String, String>> dedupStoreBuilder = Stores.keyValueStoreBuilder(
                Stores.persistentKeyValueStore(deduplicationStore),
                Serdes.String(),
                Serdes.String()
        );
        builder.addStateStore(dedupStoreBuilder);
        StationDeduplicationTransformer stationDeduplicationTransformer = new StationDeduplicationTransformer(deduplicationStore);

        var availabilityStream = builder.stream(topicProps.getAvailabilityTopic(), Consumed.with(Serdes.String(), StreamUtils.<AvroRealTimeAvailability>AvroSerde()));
        var characteristicsTable = builder.table(topicProps.getStationsCharacteristicsTopic(), Consumed.with(Serdes.String(),StreamUtils.<AvroStationCharacteristics>AvroSerde()));

        return availabilityStream
                .join(characteristicsTable, CreateStation())
                .transformValues(() -> stationDeduplicationTransformer, deduplicationStore)
                .filter((k, v) -> v != null);

    }

    private KStream<String, AvroStation> buildStationsWithStaleTimestampStream(KStream<String, AvroStation> stationChangesStream){
        return stationChangesStream
                .groupByKey(Grouped.with(Serdes.String(), StreamUtils.AvroSerde()))
                .reduce(this::KeepNewestStationAndCheckIfNumberOfBikesIsTheSame)
                .toStream();
    }

    protected AvroStation KeepNewestStationAndCheckIfNumberOfBikesIsTheSame(AvroStation stationV1, AvroStation stationV2) {
        var newest = stationV1.getLoadTimestamp()>stationV2.getLoadTimestamp()?stationV1:stationV2;
        var oldest = stationV1.getLoadTimestamp()<stationV2.getLoadTimestamp()?stationV1:stationV2;

        if(newest.getElectricBikesAtStation() == oldest.getElectricBikesAtStation()
                && newest.getMechanicalBikesAtStation() == oldest.getMechanicalBikesAtStation()){
            newest.setStaleSinceTimestamp(oldest.getStaleSinceTimestamp());
        }
        return newest;
    }

    protected ValueJoiner<AvroRealTimeAvailability, AvroStationCharacteristics, AvroStation> CreateStation() {
        return (a, c) -> AvroStation.newBuilder()
                .setStationCode(c.getStationCode())
                .setStationName(c.getStationName())
                .setTotalCapacity(c.getTotalCapacity())
                .setLatitude(c.getLatitude())
                .setLongitude(c.getLongitude())
                .setElectricBikesAtStation(a.getElectricBikesAtStation())
                .setMechanicalBikesAtStation(a.getMechanicalBikesAtStation())
                .setAvailabilityTimestamp(a.getAvailabilityTimestamp())
                .setLoadTimestamp(a.getLoadTimestamp())
                .setStaleSinceTimestamp(null)
                .build();
    }
}