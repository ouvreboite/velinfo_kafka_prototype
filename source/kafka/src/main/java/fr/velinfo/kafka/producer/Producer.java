package fr.velinfo.kafka.producer;

import fr.velinfo.kafka.producer.mapper.AvroMapper;
import fr.velinfo.kafka.producer.mapper.KeyMapper;
import fr.velinfo.kafka.producer.mapper.TimestampMapper;
import fr.velinfo.opendata.dto.OpenDataDto;
import fr.velinfo.properties.ConnectionConfiguration;
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;
import org.apache.avro.AvroRuntimeException;
import org.apache.avro.specific.SpecificRecord;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.errors.SerializationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class Producer<P extends OpenDataDto<F>,F,A extends SpecificRecord> {
    private static final Logger LOGGER = LoggerFactory.getLogger(Producer.class);
    private final String topic;
    private final KeyMapper<A> keyExtractor;
    private final TimestampMapper<A> timestampExtractor;
    private final KafkaProducer<String, A> kafkaProducer;
    private final AvroMapper<F, A> avroMapper;


    public Producer(String topic, KeyMapper<A> keyExtractor, TimestampMapper<A> timestampExtractor, AvroMapper<F, A> avroMapper, ConnectionConfiguration config) {
        this.topic = topic;
        this.keyExtractor = keyExtractor;
        this.timestampExtractor = timestampExtractor;
        this.avroMapper = avroMapper;
        this.kafkaProducer = initProducer(config);
    }

    private KafkaProducer<String, A> initProducer(ConnectionConfiguration config) {
        var props = new Properties();

        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, config.getBootstrapServers());
        props.put(KafkaAvroDeserializerConfig.SCHEMA_REGISTRY_URL_CONFIG, config.getSchemaRegistryUrl());
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "io.confluent.kafka.serializers.KafkaAvroSerializer");
        props.put(ProducerConfig.MAX_BLOCK_MS_CONFIG, "5000");

        var kafkaProducer = new KafkaProducer<String, A>(props);
        Runtime.getRuntime().addShutdownHook(new Thread(kafkaProducer::close, "Shutdown-thread"));
        return kafkaProducer;
    }

    public void send(P payload) {

        try{
            List<ProducerRecord<String, A>> kafkaRecords = payload.getRecords().stream()
                    .map(record -> avroMapper.map(record.getFields()))
                    .map(avroRecord -> new ProducerRecord<>(topic, null, timestampExtractor.extractTimestamp(avroRecord),keyExtractor.extractKey(avroRecord), avroRecord))
                    .collect(Collectors.toList());

            for(var kafkaRecord : kafkaRecords){
                kafkaProducer.send(kafkaRecord).get();
            }

        }catch(AvroRuntimeException e){
            LOGGER.error("Avro error",e);
        }catch (InterruptedException | ExecutionException e) {
            LOGGER.error("Error pushing to Kafka",e);
        }catch (SerializationException e) {
            LOGGER.error("Error during deserialization",e);
        }

        kafkaProducer.flush();
    }
}
