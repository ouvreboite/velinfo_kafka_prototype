package velibstreaming.producer.client.dto;

import lombok.Data;

import java.util.Date;

@Data
    public class BicycleCount extends OpenDataDto<BicycleCount.BicycleCountFields> {
    @Data
    public static class BicycleCountFields{
        private String id_compteur;
        private int sum_counts;
        private Date date;
    }
}
