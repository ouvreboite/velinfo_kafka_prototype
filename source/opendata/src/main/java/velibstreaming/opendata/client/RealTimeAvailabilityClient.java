package velibstreaming.opendata.client;

import velibstreaming.opendata.dto.RealTimeAvailability;

public class RealTimeAvailabilityClient extends OpenDataClient<RealTimeAvailability> {
    public RealTimeAvailabilityClient() {
        super(RealTimeAvailability.class, "velib-disponibilite-en-temps-reel");
    }
}
