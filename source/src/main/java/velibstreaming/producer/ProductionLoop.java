package velibstreaming.producer;

import velibstreaming.properties.StreamProperties;
import velibstreaming.producer.client.*;
import velibstreaming.producer.mapper.*;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

import static velibstreaming.producer.client.BicycleCountClient.DATE_PARAMETER;

public class ProductionLoop {

    public static void main(String[] args) {
        new ProductionLoop().startProduction();
    }

    public void startProduction() {
        StreamProperties props = StreamProperties.getInstance();

        new ProductionThread<>(props.getAvailabilityPeriodSeconds(),
                new RealTimeAvailabilityClient(),
                new Producer<>(
                        props.getAvailabilityTopic(),
                        record -> record.getStationCode().toString(),
                        new RealTimeAvailabilityMapper()))
                .start();

        new ProductionThread<>(props.getStationsCharacteristicsPeriodSeconds(),
                new StationCharacteristicsClient(),
                new Producer<>(
                        props.getStationsCharacteristicsTopic(),
                        record -> record.getStationCode().toString(),
                        new StationCharacteristicsMapper()))
                .start();

        new ProductionThread<>(props.getRoadWorkPeriodSeconds(),
                new RoadWorkClient(),
                new Producer<>(
                        props.getRoadWorkTopic(),
                        record -> record.getId().toString(),
                        new RoadWorkMapper()))
                .start();

        new ProductionThread<>(props.getCounterCharacteristicsPeriodSeconds(),
                new BicycleCounterCharacteristicsClient(),
                new Producer<>(
                        props.getCounterCharacteristicsTopic(),
                        record -> record.getCounterId().toString(),
                        new BicycleCounterCharacteristicsMapper()))
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
