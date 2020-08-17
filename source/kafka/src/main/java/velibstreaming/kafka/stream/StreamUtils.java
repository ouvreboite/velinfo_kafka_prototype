package velibstreaming.kafka.stream;

import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import org.apache.avro.specific.SpecificRecord;
import velibstreaming.properties.StreamProperties;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoField;
import java.util.Collections;
import java.util.Map;

public class StreamUtils {
    public static <T extends SpecificRecord> SpecificAvroSerde<T> AvroSerde() {
        var serde = new SpecificAvroSerde<T>();
        var serdeConfig = BuildSerdeConfig();
        serde.configure(serdeConfig, false);
        return serde;
    }

    private static Map<String, String> BuildSerdeConfig() {
        return Collections.singletonMap(KafkaAvroDeserializerConfig.SCHEMA_REGISTRY_URL_CONFIG,
                StreamProperties.getInstance().getSchemaRegistryUrl());
    }

    public static int getHour(long timestamp) {
        LocalDateTime hourlyStatsStart = LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.of("Europe/Paris"));
        return hourlyStatsStart.get(ChronoField.HOUR_OF_DAY);
    }

    public static LocalDateTime toLocalDateTime(long timestamp){
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.of("Europe/Paris"));
    }

    public static LocalDateTime now(){
        return LocalDateTime.now(ZoneId.of("Europe/Paris"));
    }
}
