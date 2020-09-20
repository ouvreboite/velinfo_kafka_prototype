package fr.velinfo.kafka.sink;

import fr.velinfo.kafka.stream.AvroSerdeBuilder;
import fr.velinfo.properties.ConnectionConfiguration;
import fr.velinfo.properties.Topics;
import fr.velinfo.repository.HourlyStationStatsRepository;
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.springframework.stereotype.Component;

import java.util.Properties;
@Component
public class SinkApplication {

    private final HourlyStationStatsRepository hourlyStationStatsRepository;
    private final ConnectionConfiguration config;

    public SinkApplication(HourlyStationStatsRepository hourlyStationStatsRepository, ConnectionConfiguration config) {
        this.hourlyStationStatsRepository = hourlyStationStatsRepository;
        this.config = config;
    }

    public void start() {
        Properties properties = sinkProperties(config);
        new Thread(() -> new DbSink<>(Topics.HOURLY_STATION_STATS, hourlyStationStatsRepository, properties, new AvroSerdeBuilder(config)).pollAndSink()).start();
    }

    private static Properties sinkProperties(ConnectionConfiguration config){
        var props = new Properties();
        props.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, config.getBootstrapServers());
        props.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.setProperty(KafkaAvroDeserializerConfig.SCHEMA_REGISTRY_URL_CONFIG, config.getSchemaRegistryUrl());
        props.setProperty(ConsumerConfig.GROUP_ID_CONFIG, "db_sinks");
        props.setProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        props.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "io.confluent.kafka.serializers.KafkaAvroDeserializer");
        return props;
    }
}
