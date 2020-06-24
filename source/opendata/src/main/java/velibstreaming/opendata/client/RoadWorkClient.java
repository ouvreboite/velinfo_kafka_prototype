package velibstreaming.opendata.client;

import velibstreaming.opendata.dto.RoadWork;

public class RoadWorkClient extends OpenDataClient<RoadWork> {
    public RoadWorkClient() {
        super(RoadWork.class, "chantiers-perturbants");
    }
}
