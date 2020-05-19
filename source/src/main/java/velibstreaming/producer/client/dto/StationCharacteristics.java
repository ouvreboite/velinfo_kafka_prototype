package velibstreaming.producer.client.dto;

import lombok.Data;

@Data
public class StationCharacteristics extends OpenDataDto<StationCharacteristics.StationCharacteristicsRecordFields> {
    @Data
    public static class StationCharacteristicsRecordFields{
        private String stationcode;
        private String name;
        private double[] coordonnees_geo;
        private int capacity;
    }
}

