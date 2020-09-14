package fr.velinfo.kafka.producer;

import fr.velinfo.avro.record.source.AvroBicycleCount;
import fr.velinfo.avro.record.source.AvroStationAvailability;
import fr.velinfo.kafka.TopicCreator;
import fr.velinfo.kafka.producer.mapper.*;
import fr.velinfo.properties.DateTimeUtils;
import fr.velinfo.properties.StreamProperties;
import fr.velinfo.opendata.client.*;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.CountDownLatch;

import static fr.velinfo.opendata.client.BicycleCountClient.DATE_PARAMETER;

public class ProducerApplication {

    public static void main(String[] args) {
        new ProducerApplication().startProduction();
    }

    public void startProduction() {
        StreamProperties props = StreamProperties.getInstance();

        TopicCreator.createTopicIfNeeded(
                props.getStationAvailabilityTopic(),
                props.getBicycleCountTopic());

        new ProductionThread<>(
                Duration.ofMinutes(1),
                new RealTimeAvailabilityClient(),
                new Producer<>(
                        props.getStationAvailabilityTopic(),
                        AvroStationAvailability::getStationCode,
                        AvroStationAvailability::getLoadTimestamp,
                        new RealTimeAvailabilityMapper()))
                .start();

        new ProductionThread<>(
                Duration.ofHours(1),
                new BicycleCountClient(),
                new Producer<>(
                        props.getBicycleCountTopic(),
                        AvroBicycleCount::getCounterId,
                        AvroBicycleCount::getCountTimestamp,
                        new BicycleCountMapper()))
                .withParameter(DATE_PARAMETER,() -> DateTimeUtils.now().minusDays(1).format(DateTimeFormatter.ISO_DATE))
                .start();

        CountDownLatch doneSignal = new CountDownLatch(1);
        try {
            doneSignal.await();
        } catch (InterruptedException ignored) {
        }
    }
}
