package velibstreaming.producer.client;

import velibstreaming.producer.client.dto.BicycleCount;

public class BicycleCountClient extends OpenDataClient<BicycleCount> {
    public static final String DATE_PARAMETER = "refine.date";
    public BicycleCountClient() {
        super(BicycleCount.class, "comptage-velo-donnees-compteurs");
    }
}
