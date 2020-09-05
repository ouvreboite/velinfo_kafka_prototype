package velibstreaming.kafka.stream;

import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import org.apache.avro.specific.SpecificRecord;
import velibstreaming.properties.StreamProperties;

import java.util.Collections;
import java.util.Map;

public class StreamUtils {
    public static <T extends SpecificRecord> SpecificAvroSerde<T> avroSerde() {
        var serde = new SpecificAvroSerde<T>();
        var serdeConfig = buildSerdeConfig();
        serde.configure(serdeConfig, false);
        return serde;
    }

    private static Map<String, String> buildSerdeConfig() {
        return Collections.singletonMap(KafkaAvroDeserializerConfig.SCHEMA_REGISTRY_URL_CONFIG,
                StreamProperties.getInstance().getSchemaRegistryUrl());
    }
}
