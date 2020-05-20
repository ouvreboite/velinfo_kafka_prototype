package velibstreaming.producer.client;

import velibstreaming.producer.client.dto.StationCharacteristics;

public class StationCharacteristicsClient extends OpenDataClient<StationCharacteristics> {
    public StationCharacteristicsClient() {
        super(StationCharacteristics.class, "velib-emplacement-des-stations");
    }
}
