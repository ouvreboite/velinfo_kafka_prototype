package velibstreaming.producer.client;

import velibstreaming.producer.client.dto.RealTimeAvailability;

public class RealTimeAvailabilityClient extends OpenDataClient<RealTimeAvailability> {
    public RealTimeAvailabilityClient() {
        super(RealTimeAvailability.class, "https://opendata.paris.fr/api/records/1.0/search/?dataset=velib-disponibilite-en-temps-reel&q=&rows=10000");
    }
}
