package velibstreaming.producer.client;

import kong.unirest.Unirest;
import velibstreaming.producer.client.dto.RealTimeAvailability;

public class RealTimeAvailabilityClient {

    public RealTimeAvailability fetch(){
        return Unirest.get("https://opendata.paris.fr/api/records/1.0/search/?dataset=velib-disponibilite-en-temps-reel&q=&rows=10000")
                .header("accept", "application/json")
                .asObject(RealTimeAvailability.class)
                .ifFailure(Error.class, r -> {
                    throw r.getBody();
                })
                .getBody();
    }


}
