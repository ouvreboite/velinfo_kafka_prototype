package velibstreaming.producer;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.config.TopicConfig;
import org.apache.kafka.common.errors.TopicExistsException;
import velibstreaming.producer.client.dto.OpenDataDto;

import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;

public class Producer<P extends OpenDataDto<F>,F> {

    private final Properties kafkaProps;
    private final Properties producerProps;
    private final String topic;
    private final Function<F, String> keyExtractor;


    public Producer(Properties kafkaProps, String topic, Function<F, String> keyExtractor) {
        this.kafkaProps = kafkaProps;
        this.producerProps = BuildProducerProps(kafkaProps);
        this.topic = topic;
        this.keyExtractor = keyExtractor;
        createTopicIfNeeded();
    }

    public void createTopicIfNeeded() {
        var newTopic = new NewTopic(topic, 5, (short) 1);
        newTopic.configs(Map.of(
                TopicConfig.CLEANUP_POLICY_CONFIG,TopicConfig.CLEANUP_POLICY_COMPACT,
                TopicConfig.RETENTION_MS_CONFIG, "-1",
                TopicConfig.RETENTION_BYTES_CONFIG, "1000000"
        ));

        try (final AdminClient adminClient = AdminClient.create(kafkaProps)) {
            adminClient.createTopics(Collections.singletonList(newTopic)).all().get();
        } catch (final InterruptedException | ExecutionException e) {
            // Ignore if TopicExistsException, which may be valid if topic exists
            if (!(e.getCause() instanceof TopicExistsException)) {
                throw new RuntimeException(e);
            }
        }
    }

    public void send(P payload) {
        var producer = new KafkaProducer<String, OpenDataDto.Record<F>>(this.producerProps);

        payload.getRecords().stream()
                .map(record -> new ProducerRecord<>(topic, keyExtractor.apply(record.getFields()), record))
                .map(producer::send);

        producer.flush();
    }

    private Properties BuildProducerProps(Properties kafkaProps) {
        var props = new Properties();
        props.putAll(kafkaProps);
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "io.confluent.kafka.serializers.KafkaJsonSerializer");
        return props;
    }
}
