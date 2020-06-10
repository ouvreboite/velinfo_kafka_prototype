package velibstreaming.producer;

import lombok.Data;
import velibstreaming.producer.client.*;
import velibstreaming.producer.mapper.*;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

import static velibstreaming.producer.client.BicycleCountClient.DATE_PARAMETER;

public class ProductionLoop {

    public static void main(String[] args) throws IOException {
        new ProductionLoop().run();
    }

    public void run() throws IOException {
        var producerProps = LoadProperties("producers.properties");
        var kafkaProps = LoadProperties("kafka.properties");
        Parameters parameters = LoadParameters(producerProps);

        new ProductionThread<>(parameters.availabilityPeriodSeconds,
                new RealTimeAvailabilityClient(),
                new Producer<>(kafkaProps,
                        parameters.availabilityTopic,
                        record -> record.getStationCode().toString(),
                        new RealTimeAvailabilityMapper()))
                .start();

        new ProductionThread<>(parameters.stationsCharacteristicsPeriodSeconds,
                new StationCharacteristicsClient(),
                new Producer<>(kafkaProps,
                        parameters.stationsCharacteristicsTopic,
                        record -> record.getStationCode().toString(),
                        new StationCharacteristicsMapper()))
                .start();

        new ProductionThread<>(parameters.roadWorkPeriodSeconds,
                new RoadWorkClient(),
                new Producer<>(kafkaProps,
                        parameters.roadWorkTopic,
                        record -> record.getId().toString(),
                        new RoadWorkMapper()))
                .start();

        new ProductionThread<>(parameters.counterCharacteristicsPeriodSeconds,
                new BicycleCounterCharacteristicsClient(),
                new Producer<>(kafkaProps,
                        parameters.counterCharacteristicsTopic,
                        record -> record.getCounterId().toString(),
                        new BicycleCounterCharacteristicsMapper()))
                .start();

        new ProductionThread<>(parameters.bicycleCountPeriodSeconds,
                new BicycleCountClient(),
                new Producer<>(kafkaProps,
                        parameters.bicycleCountTopic,
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

    private Parameters LoadParameters(Properties producerProps) throws IOException {
        Parameters parameters = new Parameters();

        parameters.availabilityPeriodSeconds = Long.parseLong(producerProps.getProperty("RealTimeAvailability.Loop.Seconds", "60"));
        parameters.stationsCharacteristicsPeriodSeconds = Long.parseLong(producerProps.getProperty("StationCharacteristics.Loop.Seconds", "60"));
        parameters.roadWorkPeriodSeconds = Long.parseLong(producerProps.getProperty("RoadWork.Loop.Seconds", "60"));
        parameters.bicycleCountPeriodSeconds = Long.parseLong(producerProps.getProperty("BicycleCounterCharacteristics.Loop.Seconds", "60"));
        parameters.counterCharacteristicsPeriodSeconds = Long.parseLong(producerProps.getProperty("BicycleCount.Loop.Seconds", "60"));

        parameters.availabilityTopic = producerProps.getProperty("RealTimeAvailability.Topic");
        parameters.stationsCharacteristicsTopic = producerProps.getProperty("StationCharacteristics.Topic");
        parameters.roadWorkTopic = producerProps.getProperty("RoadWork.Topic");
        parameters.bicycleCountTopic = producerProps.getProperty("BicycleCounterCharacteristics.Topic");
        parameters.counterCharacteristicsTopic = producerProps.getProperty("BicycleCount.Topic");

        return parameters;
    }

    private Properties LoadProperties(String fileName) throws IOException {
        Properties props = new Properties();
        props.load(ProductionLoop.class.getClassLoader().getResourceAsStream(fileName));
        return props;
    }

    @Data
    private static class Parameters{
        long availabilityPeriodSeconds;
        long stationsCharacteristicsPeriodSeconds;
        long roadWorkPeriodSeconds;
        long bicycleCountPeriodSeconds;
        long counterCharacteristicsPeriodSeconds;

        String availabilityTopic;
        String stationsCharacteristicsTopic;
        String roadWorkTopic;
        String bicycleCountTopic;
        String counterCharacteristicsTopic;
    }
}
