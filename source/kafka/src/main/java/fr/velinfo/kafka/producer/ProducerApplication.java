package fr.velinfo.kafka.producer;

import fr.velinfo.avro.record.source.AvroStationAvailability;
import fr.velinfo.kafka.TopicCreator;
import fr.velinfo.kafka.producer.mapper.RealTimeAvailabilityMapper;
import fr.velinfo.opendata.client.RealTimeAvailabilityClient;
import fr.velinfo.properties.Topics;

import java.time.Duration;
import java.util.concurrent.CountDownLatch;

public class ProducerApplication {

    public static void main(String[] args) {
        new ProducerApplication().startProduction();
    }

    public void startProduction() {
        TopicCreator.createTopicIfNeeded(Topics.STATION_AVAILABILITIES);

        new ProductionThread<>(
                Duration.ofMinutes(1),
                new RealTimeAvailabilityClient(),
                new Producer<>(
                        Topics.STATION_AVAILABILITIES,
                        AvroStationAvailability::getStationCode,
                        AvroStationAvailability::getLoadTimestamp,
                        new RealTimeAvailabilityMapper()))
                .start();

        CountDownLatch doneSignal = new CountDownLatch(1);
        try {
            doneSignal.await();
        } catch (InterruptedException ignored) {
        }
    }
}
