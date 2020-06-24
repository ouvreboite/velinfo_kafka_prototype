package velibstreaming.kafka.stream;

import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import org.apache.avro.specific.SpecificRecord;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.*;
import velibstreaming.avro.record.source.AvroRealTimeAvailability;
import velibstreaming.avro.record.source.AvroStationCharacteristics;
import velibstreaming.avro.record.stream.AvroStationAvailability;
import velibstreaming.properties.StreamProperties;

import java.util.Collections;
import java.util.Map;
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
        final StreamsBuilder builder = new StreamsBuilder();

        createEnrichAvailabilitiesWithStationStream(builder, StreamProperties.getInstance().getStreamStationAvailabilityTopic());

        var props = buildStreamsProperties();
        this.streams = new KafkaStreams(builder.build(), props);
        this.streams.start();
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
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "velibstreaming");
        props.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, 0);
        props.put(KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG, "true");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        return props;
    }


    private <T extends SpecificRecord> SpecificAvroSerde<T> AvroSerde() {
        var stationCharacteristicsSerde = new SpecificAvroSerde<T>();
        var serdeConfig = BuildSerdeConfig();
        stationCharacteristicsSerde.configure(serdeConfig, false);
        return stationCharacteristicsSerde;
    }


    private Map<String, String> BuildSerdeConfig() {
        return Collections.singletonMap(KafkaAvroDeserializerConfig.SCHEMA_REGISTRY_URL_CONFIG,
                StreamProperties.getInstance().getSchemaRegistryUrl());
    }

    private void createEnrichAvailabilitiesWithStationStream(final StreamsBuilder builder, String outputTopic) {
        StreamProperties topicProps = StreamProperties.getInstance();

        var availabilities = builder.stream(topicProps.getAvailabilityTopic(), Consumed.with(Serdes.String(), this.<AvroRealTimeAvailability>AvroSerde()));
        var characteristics = builder.table(topicProps.getStationsCharacteristicsTopic(), Consumed.with(Serdes.String(),this.<AvroStationCharacteristics>AvroSerde()));

        availabilities
                .join(characteristics, MergeAvailabilityAndStation())
                .to(outputTopic, Produced.with(Serdes.String(), this.AvroSerde()));
    }

    private ValueJoiner<AvroRealTimeAvailability, AvroStationCharacteristics, AvroStationAvailability> MergeAvailabilityAndStation() {
        return (a, c) -> AvroStationAvailability.newBuilder()
                .setStationCode(c.getStationCode())
                .setStationName(c.getStationName())
                .setTotalCapacity(c.getTotalCapacity())
                .setLatitude(c.getLatitude())
                .setLongitude(c.getLongitude())
                .setElectricBikesAtStation(a.getElectricBikesAtStation())
                .setMechanicalBikesAtStation(a.getMechanicalBikesAtStation())
                .setAvailabilityTimestamp(a.getAvailabilityTimestamp())
                .build();
    }
}