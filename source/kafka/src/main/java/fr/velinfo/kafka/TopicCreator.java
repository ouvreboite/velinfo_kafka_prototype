package fr.velinfo.kafka;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.config.TopicConfig;
import org.apache.kafka.common.errors.TopicExistsException;
import fr.velinfo.properties.ConnectionConfiguration;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class TopicCreator {

    public static void createTopicIfNeeded(String... topics) {
        List<NewTopic> topicsToCreate = Arrays.stream(topics)
                .map(topic -> configureNewTopic(topic))
                .collect(Collectors.toList());

        var props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, ConnectionConfiguration.getInstance().getBootstrapServers());

        try (final AdminClient adminClient = AdminClient.create(props)) {
            adminClient.createTopics(topicsToCreate).all().get();
        } catch (final InterruptedException | ExecutionException e) {
            // Ignore if TopicExistsException, which may be valid if topic exists
            if (!(e.getCause() instanceof TopicExistsException)) {
                throw new RuntimeException(e);
            }
        }
    }

    private static final int DAY_MS = 24*3600*1000;
    private static final int MEGABYTES = 1_000_000;
    private static NewTopic configureNewTopic(String topic) {
        NewTopic newTopic = new NewTopic(topic, 5, (short) 1);
        newTopic.configs(Map.of(
                TopicConfig.CLEANUP_POLICY_CONFIG, TopicConfig.CLEANUP_POLICY_DELETE+","+TopicConfig.CLEANUP_POLICY_COMPACT,
                TopicConfig.RETENTION_MS_CONFIG, ""+(3*DAY_MS),
                TopicConfig.RETENTION_BYTES_CONFIG, ""+(100*MEGABYTES),
                TopicConfig.MAX_COMPACTION_LAG_MS_CONFIG, ""+(1*DAY_MS)
        ));
        return newTopic;
    }
}
