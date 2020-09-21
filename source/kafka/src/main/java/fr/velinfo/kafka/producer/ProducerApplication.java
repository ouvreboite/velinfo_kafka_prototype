package fr.velinfo.kafka.producer;

import fr.velinfo.opendata.client.OpenDataClient;
import fr.velinfo.opendata.client.RealTimeAvailabilityClient;
import fr.velinfo.opendata.dto.RealTimeAvailability;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.CountDownLatch;

@Component
public class ProducerApplication {
    private final RealTimeAvailabilityClient realTimeAvailabilityClient;
    private final StationAvailabilityProducer stationAvailabilityProducer;

    public ProducerApplication(StationAvailabilityProducer stationAvailabilityProducer, RealTimeAvailabilityClient realTimeAvailabilityClient) {
        this.stationAvailabilityProducer = stationAvailabilityProducer;
        this.realTimeAvailabilityClient = realTimeAvailabilityClient;
    }

    public void start() {

        new ProductionThread(
                Duration.ofMinutes(1),
                realTimeAvailabilityClient,
                stationAvailabilityProducer)
                .start();

        CountDownLatch doneSignal = new CountDownLatch(1);
        try {
            doneSignal.await();
        } catch (InterruptedException ignored) {
        }
    }
}
