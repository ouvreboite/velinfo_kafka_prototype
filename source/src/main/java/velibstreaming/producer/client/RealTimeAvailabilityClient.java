package velibstreaming.producer.client;

import velibstreaming.producer.client.dto.RealTimeAvailability;

public class RealTimeAvailabilityClient extends OpenDataClient<RealTimeAvailability> {
    public RealTimeAvailabilityClient() {
        super(RealTimeAvailability.class, "velib-disponibilite-en-temps-reel");
    }
}
