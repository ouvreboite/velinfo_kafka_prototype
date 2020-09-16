package fr.velinfo.kafka.sink;

import fr.velinfo.kafka.stream.StreamUtils;
import fr.velinfo.repository.Repository;
import org.apache.avro.specific.SpecificRecord;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.Serdes;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

public class DbSink<V extends SpecificRecord> {
    private final KafkaConsumer<String, V> consumer;
    private final Repository<V> repository;
    private final String topic;

    public DbSink(String topic, Repository<V> repository, Properties props) {
        this.consumer = initConsumer(props);
        this.topic = topic;
        this.repository = repository;
    }

    public void pollAndSink() {
        try {
            consumer.subscribe(Collections.singletonList(topic));

            while(true){
                ConsumerRecords<String, V> records = consumer.poll(Duration.ofSeconds(5));
                List<V> objects = extractValues(records);
                try{
                    repository.persist(objects);
                    consumer.commitSync();
                }catch(Repository.RepositoryException e){
                    System.out.println(e);
                }
            }
        } finally {
            consumer.close();
        }
    }

    private List<V> extractValues(ConsumerRecords<String, V> records) {
        List<V> objects = new ArrayList<>();
        for (ConsumerRecord<String, V> record : records) {
            objects.add(record.value());
        }
        return objects;
    }

    private KafkaConsumer<String, V> initConsumer(Properties props) {
        var kafkaConsumer = new KafkaConsumer<>(props, Serdes.String().deserializer(), StreamUtils.<V>avroSerde().deserializer());
        Runtime.getRuntime().addShutdownHook(new Thread(kafkaConsumer::close, "Shutdown-thread"));
        return kafkaConsumer;
    }

}
