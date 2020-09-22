package fr.velinfo.kafka.producer;

import fr.velinfo.common.ConnectionConfiguration;
import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig;
import org.apache.avro.specific.SpecificRecord;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Component
public class KafkaProducerBuilder {

    private ConnectionConfiguration config;

    public KafkaProducerBuilder(ConnectionConfiguration config) {
        this.config = config;
    }

    public <A extends SpecificRecord> KafkaProducer<String, A> createProducer() {
        var props = new Properties();

        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, config.getBootstrapServers());
        props.put(AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, config.getSchemaRegistryUrl());
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "io.confluent.kafka.serializers.KafkaAvroSerializer");
        props.put(ProducerConfig.MAX_BLOCK_MS_CONFIG, "5000");

        var kafkaProducer = new KafkaProducer<String, A>(props);
        Runtime.getRuntime().addShutdownHook(new Thread(kafkaProducer::close, "Shutdown-thread"));
        return kafkaProducer;
    }
}
