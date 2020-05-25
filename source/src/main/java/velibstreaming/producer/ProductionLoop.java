package velibstreaming.producer;

import lombok.Data;
import velibstreaming.producer.client.*;
import velibstreaming.producer.client.dto.OpenDataDto;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.function.Supplier;

import static velibstreaming.producer.client.BicycleCountClient.DATE_PARAMETER;

public class ProductionLoop {

    public static void main(String[] args) throws IOException {
        Parameters parameters = LoadParameters();

        new LoopThread<>(parameters.availabilityPeriodSeconds, new RealTimeAvailabilityClient())
            .start();

        new LoopThread<>(parameters.stationsCharacteristicsPeriodSeconds, new StationCharacteristicsClient())
            .start();

        new LoopThread<>(parameters.roadWorkPeriodSeconds, new RoadWorkClient())
            .start();

        new LoopThread<>(parameters.counterCharacteristicsPeriodSeconds, new BicycleCounterCharacteristicsClient())
            .start();

        new LoopThread<>(parameters.bicycleCountPeriodSeconds, new BicycleCountClient())
            .withParameter(DATE_PARAMETER,() -> LocalDate.now().minusDays(1).format(DateTimeFormatter.ISO_DATE))
            .start();

        CountDownLatch doneSignal = new CountDownLatch(1);
        try {
            doneSignal.await();
        } catch (InterruptedException e) {
        }
    }

    private static Parameters LoadParameters() throws IOException {
        System.out.println("Loading properties");
        Properties props = new Properties();
        props.load(ProductionLoop.class.getClassLoader().getResourceAsStream("producers.properties"));

        Parameters parameters = new Parameters();

        parameters.availabilityPeriodSeconds = Long.parseLong(props.getProperty("RealTimeAvailabilityClient.Loop.Seconds", "60"));
        parameters.stationsCharacteristicsPeriodSeconds = Long.parseLong(props.getProperty("StationCharacteristicsClient.Loop.Seconds", "60"));
        parameters.roadWorkPeriodSeconds = Long.parseLong(props.getProperty("RoadWorkClient.Loop.Seconds", "60"));
        parameters.bicycleCountPeriodSeconds = Long.parseLong(props.getProperty("BicycleCounterCharacteristicsClient.Loop.Seconds", "60"));
        parameters.counterCharacteristicsPeriodSeconds = Long.parseLong(props.getProperty("BicycleCountClient.Loop.Seconds", "60"));

        return parameters;
    }

    @Data
    private static class Parameters{
        long availabilityPeriodSeconds;
        long stationsCharacteristicsPeriodSeconds;
        long roadWorkPeriodSeconds;
        long bicycleCountPeriodSeconds;
        long counterCharacteristicsPeriodSeconds;
    }

    private static class LoopThread<T extends OpenDataDto> extends Thread{
        private final long loopPeriodSeconds;
        private OpenDataClient<T> client;

        public LoopThread(long loopPeriodSeconds, OpenDataClient<T> client) {
            super();
            this.loopPeriodSeconds = loopPeriodSeconds;
            this.client = client;
            this.setDaemon(true);
            System.out.println("Fetching data from : "+client.getClass()+" every "+loopPeriodSeconds+" seconds");
        }

        @Override
        public void run() {
            try {
                while(true){
                    T result = client.get();
                    System.out.println("Fetched "+result.getRecords().size()+" "+result.getClass());
                    Thread.sleep(loopPeriodSeconds*1_000);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        public LoopThread<T> withParameter(String parameter, Supplier<String> valueSupplier){
            this.client.withParameter(parameter, valueSupplier.get());
            return this;
        }
    }
}
