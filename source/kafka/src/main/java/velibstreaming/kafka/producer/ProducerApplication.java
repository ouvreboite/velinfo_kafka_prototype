package velibstreaming.kafka.producer;

import velibstreaming.avro.record.source.AvroBicycleCount;
import velibstreaming.avro.record.source.AvroStationAvailability;
import velibstreaming.kafka.producer.mapper.*;
import velibstreaming.properties.StreamProperties;
import velibstreaming.opendata.client.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.CountDownLatch;

import static velibstreaming.opendata.client.BicycleCountClient.DATE_PARAMETER;

public class ProducerApplication {

    public static void main(String[] args) {
        new ProducerApplication().startProduction();
    }

    public void startProduction() {
        StreamProperties props = StreamProperties.getInstance();

        new ProductionThread<>(props.getAvailabilityPeriodSeconds(),
                new RealTimeAvailabilityClient(),
                new Producer<>(
                        props.getStationAvailabilityTopic(),
                        AvroStationAvailability::getStationCode,
                        AvroStationAvailability::getLoadTimestamp,
                        new RealTimeAvailabilityMapper()))
                .start();

        new ProductionThread<>(props.getBicycleCountPeriodSeconds(),
                new BicycleCountClient(),
                new Producer<>(
                        props.getBicycleCountTopic(),
                        AvroBicycleCount::getCounterId,
                        AvroBicycleCount::getCountTimestamp,
                        new BicycleCountMapper()))
                .withParameter(DATE_PARAMETER,() -> LocalDate.now().minusDays(1).format(DateTimeFormatter.ISO_DATE))
                .start();

        CountDownLatch doneSignal = new CountDownLatch(1);
        try {
            doneSignal.await();
        } catch (InterruptedException ignored) {
        }
    }
}
