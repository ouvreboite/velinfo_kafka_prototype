package fr.velinfo.kafka;

import fr.velinfo.kafka.producer.ProducerApplication;
import fr.velinfo.kafka.sink.SinkApplication;
import fr.velinfo.kafka.stream.StreamApplication;
import fr.velinfo.opendata.client.RealTimeAvailabilityClient;
import fr.velinfo.common.ConnectionConfiguration;
import fr.velinfo.common.Topics;
import fr.velinfo.repository.HourlyStationStatsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class KafkaApplication {
    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaApplication.class);

    private final ProducerApplication producerApplication;
    private final StreamApplication streamApplication;
    private final SinkApplication sinkApplication;
    private final TopicCreator topicCreator;

    public KafkaApplication(ProducerApplication producerApplication, StreamApplication streamApplication, SinkApplication sinkApplication, TopicCreator topicCreator) {
        this.producerApplication = producerApplication;
        this.streamApplication = streamApplication;
        this.sinkApplication = sinkApplication;
        this.topicCreator = topicCreator;
    }

    public static void main(String[] args) throws IOException {
        if(args.length != 2)
            throw new IllegalArgumentException("First argument should be the type of sub application to run (PRODUCER, STREAM or SINK). The second one should be the path to the connection.conf file");

        LOGGER.info("Applications to run: {}", args[0]);
        LOGGER.info("Connection configuration path: {}", args[1]);

        String applicationToRun = args[0];
        ConnectionConfiguration config = new ConnectionConfiguration(args[1]);

        AnnotationConfigApplicationContext context = setupSpringContext(config);
        KafkaApplication app = context.getBean(KafkaApplication.class);

        app.run(applicationToRun);
    }

    private static AnnotationConfigApplicationContext setupSpringContext(ConnectionConfiguration config) {
        var context = new AnnotationConfigApplicationContext();
        context.registerBean(ConnectionConfiguration.class, () -> config);
        context.registerBean(RealTimeAvailabilityClient.class, RealTimeAvailabilityClient::new);
        context.registerBean(HourlyStationStatsRepository.class, ()  -> new HourlyStationStatsRepository(config));
        context.scan(KafkaApplication.class.getPackageName());
        context.refresh();
        return context;
    }

    public void run(String applicationToRun){
        LOGGER.info("Creating topics if needed");
        topicCreator.createTopicIfNeeded(
                Topics.STATION_AVAILABILITIES,
                Topics.STATION_UPDATES,
                Topics.HOURLY_STATION_STATS,
                Topics.BIKES_LOCKED,
                Topics.STATION_STATUS
        );


        LOGGER.info("Launch application {}", applicationToRun);
        switch (applicationToRun){
            case "PRODUCER": producerApplication.start(); break;
            case "STREAM": streamApplication.start(); break;
            case "SINK": sinkApplication.start(); break;
            default: throw new IllegalArgumentException("Unknown application : "+applicationToRun);
        }
    }
}
