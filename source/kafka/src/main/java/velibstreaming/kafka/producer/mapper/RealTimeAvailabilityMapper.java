package velibstreaming.kafka.producer.mapper;

import velibstreaming.avro.record.AvroRealTimeAvailability;
import velibstreaming.opendata.dto.RealTimeAvailability;

public class RealTimeAvailabilityMapper implements AvroMapper<RealTimeAvailability.Fields, AvroRealTimeAvailability> {
    @Override
    public AvroRealTimeAvailability map(RealTimeAvailability.Fields record) {
        return AvroRealTimeAvailability.newBuilder()
                .setStationCode(record.getStationcode())
                .setAvailabilityTimestamp(record.getDuedate().getTime())
                .setElectricBikesAtStation(record.getEbike())
                .setMechanicalBikesAtStation(record.getMechanical())
                .build();
    }
}
