package velibstreaming.producer.client;

import velibstreaming.producer.client.dto.RoadWork;

public class RoadWorkClient extends OpenDataClient<RoadWork> {
    public RoadWorkClient() {
        super(RoadWork.class, "chantiers-perturbants");
    }
}
