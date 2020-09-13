package fr.velinfo.opendata.client;

import fr.velinfo.opendata.dto.RealTimeAvailability;

public class RealTimeAvailabilityClient extends OpenDataClient<RealTimeAvailability> {
    public RealTimeAvailabilityClient() {
        super(RealTimeAvailability.class, "velib-disponibilite-en-temps-reel");
    }
}
