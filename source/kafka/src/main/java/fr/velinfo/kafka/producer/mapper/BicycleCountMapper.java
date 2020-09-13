package fr.velinfo.kafka.producer.mapper;

import fr.velinfo.avro.record.source.AvroBicycleCount;
import fr.velinfo.avro.record.source.AvroCoordinates;
import fr.velinfo.opendata.dto.BicycleCount;

public class BicycleCountMapper implements AvroMapper<BicycleCount.Fields, AvroBicycleCount> {
    @Override
    public AvroBicycleCount map(BicycleCount.Fields record) {
        AvroCoordinates coordinates = AvroCoordinates.newBuilder()
                .setLatitude(record.getCoordinates() != null ? record.getCoordinates()[0] : 0.0)
                .setLongitude(record.getCoordinates() != null ? record.getCoordinates()[1] : 0.0)
                .build();
        return AvroBicycleCount.newBuilder()
                .setCounterId(record.getId_compteur())
                .setCount(record.getSum_counts())
                .setCountTimestamp(record.getDate().getTime())
                .setCoordinates(coordinates)
                .build();
    }
}
