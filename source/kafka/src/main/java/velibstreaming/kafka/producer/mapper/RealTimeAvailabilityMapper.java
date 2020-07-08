package velibstreaming.kafka.producer.mapper;

import velibstreaming.avro.record.source.AvroStationAvailability;
import velibstreaming.opendata.dto.RealTimeAvailability;

import java.util.Date;

public class RealTimeAvailabilityMapper implements AvroMapper<RealTimeAvailability.Fields, AvroStationAvailability> {
    @Override
    public AvroStationAvailability map(RealTimeAvailability.Fields record) {
        return AvroStationAvailability.newBuilder()
                .setStationCode(record.getStationcode())
                .setAvailabilityTimestamp(record.getDuedate().getTime())
                .setElectricBikesAtStation(record.getEbike())
                .setMechanicalBikesAtStation(record.getMechanical())
                .setLoadTimestamp(new Date().getTime())
                .setStationName(record.getName())
                .setStationCapacity(record.getCapacity())
                .setLatitude(record.getCoordonnees_geo() != null ? record.getCoordonnees_geo()[0] : 0.0)
                .setLongitude(record.getCoordonnees_geo() != null ? record.getCoordonnees_geo()[1] : 0.0)
                .setIsInstalled("OUI".equals(record.getIs_installed()))
                .setIsRenting("OUI".equals(record.getIs_renting()))
                .setIsReturning("OUI".equals(record.getIs_returning()))
                .build();
    }
}
