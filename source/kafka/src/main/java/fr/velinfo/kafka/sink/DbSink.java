package fr.velinfo.kafka.sink;

import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;
import org.apache.avro.specific.SpecificRecord;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.Serdes;
import fr.velinfo.repository.Repository;
import fr.velinfo.kafka.stream.StreamUtils;
import fr.velinfo.properties.StreamProperties;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

public class DbSink<V extends SpecificRecord> {
    private final KafkaConsumer<String, V> consumer;
    private final Repository<V> repository;
    private final String topic;

    public DbSink(String topic, Repository<V> repository) {
        this.consumer = initConsumer();
        this.topic = topic;
        this.repository = repository;
    }

    public void pollAndSink() {
        try {
            consumer.subscribe(Collections.singletonList(topic));

            while(true){
                ConsumerRecords<String, V> records = consumer.poll(Duration.ofSeconds(5));
                try{
                    for (ConsumerRecord<String, V> record : records) {
                        repository.persist(record.value());
                    }
                    consumer.commitSync();
                }catch(Repository.RepositoryException e){
                    System.out.println(e);
                }
            }
        } finally {
            consumer.close();
        }
    }

    private KafkaConsumer<String, V> initConsumer() {
        var props = new Properties();
        props.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, StreamProperties.getInstance().getBootstrapServers());
        props.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.setProperty(KafkaAvroDeserializerConfig.SCHEMA_REGISTRY_URL_CONFIG, StreamProperties.getInstance().getSchemaRegistryUrl());
        props.setProperty(ConsumerConfig.GROUP_ID_CONFIG, "db_sinkss");
        props.setProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        props.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "io.confluent.kafka.serializers.KafkaAvroDeserializer");

        var kafkaConsumer = new KafkaConsumer<>(props, Serdes.String().deserializer(), StreamUtils.<V>avroSerde().deserializer());
        Runtime.getRuntime().addShutdownHook(new Thread(kafkaConsumer::close, "Shutdown-thread"));
        return kafkaConsumer;
    }

}
