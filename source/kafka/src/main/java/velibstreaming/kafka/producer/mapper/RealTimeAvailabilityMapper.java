package velibstreaming.kafka.producer.mapper;

import velibstreaming.avro.record.source.AvroRealTimeAvailability;
import velibstreaming.opendata.dto.RealTimeAvailability;

import java.time.LocalDateTime;
import java.util.Date;

public class RealTimeAvailabilityMapper implements AvroMapper<RealTimeAvailability.Fields, AvroRealTimeAvailability> {
    @Override
    public AvroRealTimeAvailability map(RealTimeAvailability.Fields record) {
        return AvroRealTimeAvailability.newBuilder()
                .setStationCode(record.getStationcode())
                .setAvailabilityTimestamp(record.getDuedate().getTime())
                .setElectricBikesAtStation(record.getEbike())
                .setMechanicalBikesAtStation(record.getMechanical())
                .setLoadTimestamp(new Date().getTime())
                .build();
    }
}
