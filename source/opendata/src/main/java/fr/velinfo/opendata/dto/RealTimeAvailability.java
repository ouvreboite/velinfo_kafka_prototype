package fr.velinfo.opendata.dto;

import lombok.Data;

import java.util.Date;

@Data
public class RealTimeAvailability extends OpenDataDto<RealTimeAvailability.Fields> {
    @Data
    public static class Fields {
        private int capacity;
        private int mechanical;
        private int ebike;
        private String stationcode;
        private Date duedate;
        private String is_installed;
        private String is_renting;
        private String is_returning;
        private double[] coordonnees_geo;
        private String name;
    }
}
