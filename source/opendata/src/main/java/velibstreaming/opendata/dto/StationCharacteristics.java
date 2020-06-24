package velibstreaming.opendata.dto;

import lombok.Data;

@Data
public class StationCharacteristics extends OpenDataDto<StationCharacteristics.Fields> {
    @Data
    public static class Fields {
        private String stationcode;
        private String name;
        private double[] coordonnees_geo;
        private int capacity;
    }
}

