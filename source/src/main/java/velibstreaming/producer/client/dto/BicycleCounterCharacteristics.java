package velibstreaming.producer.client.dto;

import lombok.Data;

@Data
public class BicycleCounterCharacteristics extends OpenDataDto<BicycleCounterCharacteristics.Fields> {
    @Data
    public static class Fields{
        private String id_compteur;
        private double[] coordinates;
    }
}

