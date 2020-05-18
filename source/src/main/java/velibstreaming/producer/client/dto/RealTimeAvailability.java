package velibstreaming.producer.client.dto;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class RealTimeAvailability {
    private List<RealTimeAvailabilityRecord> records;

    @Data
    public static class RealTimeAvailabilityRecord{
        private RealTimeAvailabilityRecordFields fields;
    }

    @Data
    public static class RealTimeAvailabilityRecordFields{
        private int capacity;
        private int mechanical;
        private int ebike;
        private String stationcode;
        private Date duedate;
    }
}
