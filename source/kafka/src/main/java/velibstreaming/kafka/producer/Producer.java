package velibstreaming.kafka.producer;

import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;
import org.apache.avro.AvroRuntimeException;
import org.apache.avro.specific.SpecificRecord;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.config.TopicConfig;
import org.apache.kafka.common.errors.TopicExistsException;
import velibstreaming.kafka.TopicCreator;
import velibstreaming.kafka.producer.mapper.AvroMapper;
import velibstreaming.kafka.producer.mapper.TimestampMapper;
import velibstreaming.opendata.dto.OpenDataDto;
import velibstreaming.kafka.producer.mapper.KeyMapper;
import velibstreaming.properties.StreamProperties;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class Producer<P extends OpenDataDto<F>,F,A extends SpecificRecord> {

    private final String topic;
    private final KeyMapper<A> keyExtractor;
    private final TimestampMapper<A> timestampExtractor;
    private final KafkaProducer<String, A> kafkaProducer;
    private final AvroMapper<F, A> avroMapper;


    public Producer(String topic, KeyMapper<A> keyExtractor, TimestampMapper<A> timestampExtractor, AvroMapper<F, A> avroMapper) {
        this.topic = topic;
        this.keyExtractor = keyExtractor;
        this.timestampExtractor = timestampExtractor;
        this.avroMapper = avroMapper;
        this.kafkaProducer = initProducer();
        TopicCreator.createTopicIfNeeded(this.topic);
    }

    private KafkaProducer<String, A> initProducer() {
        var props = new Properties();

        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, StreamProperties.getInstance().getBootstrapServers());
        props.put(KafkaAvroDeserializerConfig.SCHEMA_REGISTRY_URL_CONFIG, StreamProperties.getInstance().getSchemaRegistryUrl());
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
            System.err.println("Error mapping to Avro : "+e);
        }catch (InterruptedException | ExecutionException e) {
            System.err.println("Error pushing to Kafka : "+e);
        }

        kafkaProducer.flush();
    }
}
