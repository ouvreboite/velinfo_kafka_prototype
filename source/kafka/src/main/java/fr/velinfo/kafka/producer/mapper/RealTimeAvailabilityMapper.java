package fr.velinfo.kafka.producer.mapper;

import fr.velinfo.avro.record.source.AvroCoordinates;
import fr.velinfo.avro.record.source.AvroStationAvailability;
import fr.velinfo.opendata.dto.RealTimeAvailability;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class RealTimeAvailabilityMapper{

    public synchronized AvroStationAvailability map(RealTimeAvailability.Fields record) {
        AvroCoordinates coordinates = AvroCoordinates.newBuilder()
                .setLatitude(record.getCoordonnees_geo() != null ? record.getCoordonnees_geo()[0] : 0.0)
                .setLongitude(record.getCoordonnees_geo() != null ? record.getCoordonnees_geo()[1] : 0.0)
                .build();
        return AvroStationAvailability.newBuilder()
                .setStationCode(record.getStationcode())
                .setAvailabilityTimestamp(record.getDuedate().getTime())
                .setElectricBikesAtStation(record.getEbike())
                .setMechanicalBikesAtStation(record.getMechanical())
                .setLoadTimestamp(new Date().getTime())
                .setStationName(record.getName())
                .setStationCapacity(record.getCapacity())
                .setCoordinates(coordinates)
                .setIsInstalled("OUI".equals(record.getIs_installed()))
                .setIsRenting("OUI".equals(record.getIs_renting()))
                .setIsReturning("OUI".equals(record.getIs_returning()))
                .build();
    }
}
