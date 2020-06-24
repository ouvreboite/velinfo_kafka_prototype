package velibstreaming.opendata.client;

import velibstreaming.opendata.dto.StationCharacteristics;

public class StationCharacteristicsClient extends OpenDataClient<StationCharacteristics> {
    public StationCharacteristicsClient() {
        super(StationCharacteristics.class, "velib-emplacement-des-stations");
    }
}
