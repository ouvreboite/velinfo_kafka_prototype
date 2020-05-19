package velibstreaming.producer.client;

import velibstreaming.producer.client.dto.StationCharacteristics;

public class StationCharacteristicsClient extends OpenDataClient<StationCharacteristics> {
    public StationCharacteristicsClient() {
        super(StationCharacteristics.class, "https://opendata.paris.fr/api/records/1.0/search/?dataset=velib-emplacement-des-stations&q=&rows=10000");
    }
}
