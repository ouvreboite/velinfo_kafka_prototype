package fr.velinfo.kafka.producer;

import fr.velinfo.opendata.client.OpenDataClient;
import fr.velinfo.opendata.client.RealTimeAvailabilityClient;
import fr.velinfo.opendata.dto.RealTimeAvailability;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

public class ProductionThread extends Thread{
    private static final Logger LOGGER = LoggerFactory.getLogger(ProductionThread.class);
    private final Duration loopDuration;
    private final RealTimeAvailabilityClient client;
    private final StationAvailabilityProducer producer;

    public ProductionThread(Duration loopDuration, RealTimeAvailabilityClient client, StationAvailabilityProducer producer) {
        super();
        this.loopDuration = loopDuration;
        this.client = client;
        this.producer = producer;
        this.setDaemon(true);
        LOGGER.info("Producing {} every {}", client.getClass(),humanReadableFormat(loopDuration));
    }

    private static String humanReadableFormat(Duration duration) {
        return duration.toString()
                .substring(2)
                .replaceAll("(\\d[HMS])(?!$)", "$1 ")
                .toLowerCase();
    }

    @Override
    public void run() {
        try {
            while(true){
                try{
                    RealTimeAvailability result = client.get();
                    LOGGER.info("{} {} loaded from API", result.getRecords().size(), result.getClass());
                    if(!result.getRecords().isEmpty()){
                        producer.send(result);
                        LOGGER.info("{} {} pushed to Kafka", result.getRecords().size(), result.getClass());
                    }

                }catch(OpenDataClient.OpenDataException e){
                    LOGGER.error("Error while polling Paris API",e);
                }

                TimeUnit.SECONDS.sleep(loopDuration.toSeconds());
            }
        } catch (InterruptedException e) {
            LOGGER.error("Error during production",e);
        }
    }
}
