package fr.velinfo.webapp.dto.mapper;

import fr.velinfo.avro.record.source.AvroCoordinates;
import fr.velinfo.avro.record.stream.AvroStationUpdate;
import fr.velinfo.common.DateTimeUtils;
import fr.velinfo.webapp.dto.Station;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class StationMapperTest {
    private StationMapper mapper = new StationMapper();
    private LocalDateTime now = LocalDateTime.now().withNano(0);
    @Test
    void map() {
        AvroStationUpdate avro = buildAvro();

        Station station = mapper.map(avro);

        assertEquals("A", station.getStationCode());
        assertEquals("StationA", station.getStationName());
        assertEquals(10, station.getTotalCapacity());
        assertEquals(2, station.getElectricBikes());
        assertEquals(1, station.getMechanicalBikes());
        assertEquals(7, station.getEmptySlots());
        assertEquals(11.1, station.getLatitude());
        assertEquals(22.2, station.getLongitude());
        assertEquals(now, station.getLastChange());
        assertNull(station.getStatus());
    }

    @Test
    void map_shouldUseLoadTimestamp_whenLastChangeTimestampIsNull() {
        AvroStationUpdate avro = buildAvro();
        avro.setLastChangeTimestamp(null);
        avro.setLoadTimestamp(DateTimeUtils.timestamp(now.plusHours(1)));

        Station station = mapper.map(avro);


        assertEquals(now.plusHours(1), station.getLastChange());
    }


    private AvroStationUpdate buildAvro() {
        return AvroStationUpdate.newBuilder()
                .setStationCode("A")
                .setStationName("StationA")
                .setStationCapacity(10)
                .setElectricBikesAtStation(2)
                .setMechanicalBikesAtStation(1)
                .setCoordinates(AvroCoordinates.newBuilder()
                        .setLatitude(11.1)
                        .setLongitude(22.2)
                        .build())
                .setLoadTimestamp(123L)
                .setLastChangeTimestamp(DateTimeUtils.timestamp(now))
                .setMechanicalBikesRented(0)
                .setMechanicalBikesReturned(0)
                .setElectricBikesRented(0)
                .setElectricBikesReturned(0)
                .setAvailabilityTimestamp(0)
                .setIsRenting(true)
                .setIsReturning(true)
                .setIsInstalled(true)
                .build();
    }
}