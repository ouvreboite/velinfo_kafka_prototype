package velibstreaming.kafka.producer.mapper;

import velibstreaming.avro.record.source.AvroStationCharacteristics;
import velibstreaming.opendata.dto.StationCharacteristics;

public class StationCharacteristicsMapper implements AvroMapper<StationCharacteristics.Fields, AvroStationCharacteristics> {
    @Override
    public AvroStationCharacteristics map(StationCharacteristics.Fields record) {
        return AvroStationCharacteristics.newBuilder()
                .setStationCode(record.getStationcode())
                .setStationName(record.getName())
                .setTotalCapacity(record.getCapacity())
                .setLatitude(record.getCoordonnees_geo() != null ? record.getCoordonnees_geo()[0] : 0.0)
                .setLongitude(record.getCoordonnees_geo() != null ? record.getCoordonnees_geo()[1] : 0.0)
                .build();
    }
}
