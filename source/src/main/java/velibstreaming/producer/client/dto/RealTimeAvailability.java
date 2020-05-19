package velibstreaming.producer.client.dto;

import lombok.Data;

import java.util.Date;

@Data
public class RealTimeAvailability extends OpenDataDto<RealTimeAvailability.RealTimeAvailabilityFields> {
    @Data
    public static class RealTimeAvailabilityFields{
        private int capacity;
        private int mechanical;
        private int ebike;
        private String stationcode;
        private Date duedate;
    }
}
