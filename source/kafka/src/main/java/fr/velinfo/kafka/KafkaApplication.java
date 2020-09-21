package fr.velinfo.kafka;

import fr.velinfo.kafka.producer.ProducerApplication;
import fr.velinfo.kafka.sink.SinkApplication;
import fr.velinfo.kafka.stream.StreamApplication;
import fr.velinfo.opendata.client.RealTimeAvailabilityClient;
import fr.velinfo.properties.ConnectionConfiguration;
import fr.velinfo.properties.Topics;
import fr.velinfo.repository.HourlyStationStatsRepository;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class KafkaApplication {

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
            throw new IllegalArgumentException("First argument should be the type of sub application to run (PRODUCER,STREAM,SINK ,or comma separated mix). The second one should be the path to the connection.conf file");

        System.out.println("Applications to run: "+args[0]);
        System.out.println("Connection configuration path: "+args[1]);

        String[] applicationsToRun = args[0].split(",");
        ConnectionConfiguration config = new ConnectionConfiguration(args[1]);

        var context = new AnnotationConfigApplicationContext();
        context.registerBean(ConnectionConfiguration.class, () -> config);
        context.registerBean(RealTimeAvailabilityClient.class, RealTimeAvailabilityClient::new);
        context.registerBean(HourlyStationStatsRepository.class, ()  -> new HourlyStationStatsRepository(config));
        context.scan("fr.velinfo.kafka");
        context.refresh();
        KafkaApplication app = context.getBean(KafkaApplication.class);

        app.run(applicationsToRun);
    }

    public void run(String[] applicationsToRun){

        topicCreator.createTopicIfNeeded(
                Topics.STATION_AVAILABILITIES,
                Topics.STATION_UPDATES,
                Topics.HOURLY_STATION_STATS,
                Topics.BIKES_LOCKED,
                Topics.STATION_STATUS
        );

        for(String app : applicationsToRun){
            switch (app){
                case "PRODUCER": producerApplication.start(); break;
                case "STREAM": streamApplication.start(); break;
                case "SINK": sinkApplication.start(); break;
                default: throw new IllegalArgumentException("Unknown application : "+app);
            }
        }
    }
}
