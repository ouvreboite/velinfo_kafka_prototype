package velibstreaming.opendata.client;

import velibstreaming.opendata.dto.BicycleCounterCharacteristics;

public class BicycleCounterCharacteristicsClient extends OpenDataClient<BicycleCounterCharacteristics> {
    public BicycleCounterCharacteristicsClient() {
        super(BicycleCounterCharacteristics.class, "comptage-velo-compteurs");
    }
}
