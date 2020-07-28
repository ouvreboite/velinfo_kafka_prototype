package velibstreaming.kafka.producer;

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
                        record -> record.getStationCode().toString(),
                        new RealTimeAvailabilityMapper()))
                .start();

        new ProductionThread<>(props.getBicycleCountPeriodSeconds(),
                new BicycleCountClient(),
                new Producer<>(
                        props.getBicycleCountTopic(),
                        record -> record.getCounterId().toString(),
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
