package velibstreaming.producer.client;

import velibstreaming.producer.client.dto.BicycleCounterCharacteristics;

public class BicycleCounterCharacteristicsClient extends OpenDataClient<BicycleCounterCharacteristics> {
    public BicycleCounterCharacteristicsClient() {
        super(BicycleCounterCharacteristics.class, "comptage-velo-compteurs");
    }
}
