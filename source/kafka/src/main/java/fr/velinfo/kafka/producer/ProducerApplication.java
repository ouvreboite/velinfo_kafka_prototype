package fr.velinfo.kafka.producer;

import fr.velinfo.avro.record.source.AvroStationAvailability;
import fr.velinfo.kafka.producer.mapper.RealTimeAvailabilityMapper;
import fr.velinfo.opendata.client.OpenDataClient;
import fr.velinfo.opendata.client.RealTimeAvailabilityClient;
import fr.velinfo.opendata.dto.RealTimeAvailability;
import fr.velinfo.properties.ConnectionConfiguration;
import fr.velinfo.properties.Topics;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.CountDownLatch;

@Component
public class ProducerApplication {
    private final ConnectionConfiguration config;
    private final RealTimeAvailabilityMapper mapper;
    private final OpenDataClient<RealTimeAvailability> realTimeAvailabilityClient;

    public ProducerApplication(ConnectionConfiguration config, RealTimeAvailabilityMapper mapper, RealTimeAvailabilityClient realTimeAvailabilityClient) {
        this.config = config;
        this.mapper = mapper;
        this.realTimeAvailabilityClient = realTimeAvailabilityClient;
    }

    public void start() {

        var availabilityProducer = new Producer<RealTimeAvailability, RealTimeAvailability.Fields, AvroStationAvailability>(
                Topics.STATION_AVAILABILITIES,
                AvroStationAvailability::getStationCode,
                AvroStationAvailability::getLoadTimestamp,
                mapper,
                config);

        new ProductionThread<>(
                Duration.ofMinutes(1),
                realTimeAvailabilityClient,
                availabilityProducer)
                .start();

        CountDownLatch doneSignal = new CountDownLatch(1);
        try {
            doneSignal.await();
        } catch (InterruptedException ignored) {
        }
    }
}
