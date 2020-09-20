package fr.velinfo.kafka.stream;

import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import org.apache.avro.specific.SpecificRecord;
import fr.velinfo.properties.ConnectionConfiguration;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;
@Component
public class AvroSerdeBuilder {

    private final ConnectionConfiguration config;

    public AvroSerdeBuilder(ConnectionConfiguration config) {
        this.config = config;
    }

    public <T extends SpecificRecord> SpecificAvroSerde<T> avroSerde() {
        var serde = new SpecificAvroSerde<T>();
        var serdeConfig = Collections.singletonMap(
                KafkaAvroDeserializerConfig.SCHEMA_REGISTRY_URL_CONFIG, config.getSchemaRegistryUrl());
        serde.configure(serdeConfig, false);
        return serde;
    }
}
