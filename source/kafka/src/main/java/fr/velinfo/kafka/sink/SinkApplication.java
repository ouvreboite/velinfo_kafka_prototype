package fr.velinfo.kafka.sink;

import fr.velinfo.properties.ConnectionConfiguration;
import fr.velinfo.properties.Topics;
import fr.velinfo.repository.HourlyStationStatsRepository;
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;
import org.apache.kafka.clients.consumer.ConsumerConfig;

import java.util.Properties;

public class SinkApplication {

    public static void main(final String[] args) {
        new Thread(() -> new DbSink<>(Topics.HOURLY_STATION_STATS, new HourlyStationStatsRepository(), sinkProperties()).pollAndSink()).start();
    }

    private static Properties sinkProperties(){
        var props = new Properties();
        props.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, ConnectionConfiguration.getInstance().getBootstrapServers());
        props.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.setProperty(KafkaAvroDeserializerConfig.SCHEMA_REGISTRY_URL_CONFIG, ConnectionConfiguration.getInstance().getSchemaRegistryUrl());
        props.setProperty(ConsumerConfig.GROUP_ID_CONFIG, "db_sinks");
        props.setProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        props.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "io.confluent.kafka.serializers.KafkaAvroDeserializer");
        return props;
    }
}
