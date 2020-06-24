package velibstreaming.opendata.dto;

import lombok.Data;

import java.util.Date;

@Data
    public class BicycleCount extends OpenDataDto<BicycleCount.Fields> {
    @Data
    public static class Fields {
        private String id_compteur;
        private int sum_counts;
        private Date date;
    }
}
