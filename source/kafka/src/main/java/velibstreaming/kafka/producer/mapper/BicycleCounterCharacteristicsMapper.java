package velibstreaming.kafka.producer.mapper;

import velibstreaming.avro.record.AvroBicycleCounterCharacteristics;
import velibstreaming.opendata.dto.BicycleCounterCharacteristics;

public class BicycleCounterCharacteristicsMapper implements AvroMapper<BicycleCounterCharacteristics.Fields, AvroBicycleCounterCharacteristics> {
    @Override
    public AvroBicycleCounterCharacteristics map(BicycleCounterCharacteristics.Fields record) {
        return AvroBicycleCounterCharacteristics.newBuilder()
                .setCounterId(record.getId_compteur())
                .setLatitude(record.getCoordinates() != null ? record.getCoordinates()[0] : 0.0)
                .setLongitude(record.getCoordinates() != null ? record.getCoordinates()[1] : 0.0)
                .build();
    }
}
