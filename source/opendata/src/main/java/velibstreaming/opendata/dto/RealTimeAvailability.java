package velibstreaming.opendata.dto;

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
    }
}
