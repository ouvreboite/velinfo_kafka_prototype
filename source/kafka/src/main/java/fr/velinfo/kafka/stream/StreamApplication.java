package fr.velinfo.kafka.stream;

import fr.velinfo.common.ConnectionConfiguration;
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Properties;
import java.util.concurrent.CountDownLatch;

import static org.apache.kafka.streams.KafkaStreams.State.RUNNING;
@Component
public class StreamApplication {
    private static final Logger LOGGER = LoggerFactory.getLogger(StreamApplication.class);
    private final TopologyBuilder topologyBuilder;
    private final ConnectionConfiguration config;
    private KafkaStreams streams;

    public StreamApplication(TopologyBuilder topologyBuilder, ConnectionConfiguration config) {
        this.topologyBuilder = topologyBuilder;
        this.config = config;
    }

    public void start(){
        final CountDownLatch latch = new CountDownLatch(1);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            this.stop();
            latch.countDown();
        }));

        try {
            this.startStream(config);
            latch.await();
        } catch (InterruptedException ignored) {
        }
    }

    private void startStream(ConnectionConfiguration config) {
        Topology topology = topologyBuilder.buildTopology();
        LOGGER.info("Topology built");
        this.streams = new KafkaStreams(topology, buildStreamsProperties(config));
        this.streams.cleanUp();
        this.streams.start();
        LOGGER.info("Stream started");
    }

    public void stop() {
        if(this.streams == null || this.streams.state() != RUNNING )
            throw new IllegalStateException("The stream should be created and running before stopping it");
        this.streams.close();
    }

    private Properties buildStreamsProperties(ConnectionConfiguration config) {
        final Properties props = new Properties();
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, config.getBootstrapServers());
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "velinfo.app");
        props.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, 0);
        props.put(KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG, "true");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        return props;
    }
}