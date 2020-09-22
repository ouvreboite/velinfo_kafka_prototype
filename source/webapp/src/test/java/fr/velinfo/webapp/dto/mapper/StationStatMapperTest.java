package fr.velinfo.webapp.dto.mapper;

import fr.velinfo.avro.record.stream.AvroStationStats;
import fr.velinfo.common.DateTimeUtils;
import fr.velinfo.webapp.dto.StationStat;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StationStatMapperTest {
    private StationStatMapper mapper = new StationStatMapper();
    private LocalDateTime now = LocalDateTime.now().withNano(0);
    @Test
    void map() {
        AvroStationStats avro = buildAvro();

        StationStat stat = mapper.map(avro);

        assertEquals("a", stat.getStationCode());
        assertEquals(3, stat.getMinimumElectricBikes());
        assertEquals(4, stat.getMinimumMechanicalBikes());
        assertEquals(5, stat.getMinimumEmptySlots());
        assertEquals(6, stat.getElectricBikesRented());
        assertEquals(7, stat.getElectricBikesReturned());
        assertEquals(8, stat.getMechanicalBikesRented());
        assertEquals(9, stat.getMechanicalBikesReturned());
        assertEquals(now.minusHours(1), stat.getPeriodStart());
        assertEquals(now, stat.getPeriodEnd());
    }

    private AvroStationStats buildAvro(){
        return AvroStationStats.newBuilder()
                .setStationCode("a")
                .setLastLoadTimestamp(DateTimeUtils.timestamp(now.minusMinutes(30)))
                .setLastNumberOfElectricBikes(1)
                .setLastNumberOfMechanicalBikes(2)
                .setMinimumNumberOfElectricBikes(3)
                .setMinimumNumberOfMechanicalBikes(4)
                .setMinimumNumberOfEmptySlots(5)
                .setNumberOfElectricBikesRented(6)
                .setNumberOfElectricBikesReturned(7)
                .setNumberOfMechanicalBikesRented(8)
                .setNumberOfMechanicalBikesReturned(9)
                .setPeriodEnd(DateTimeUtils.timestamp(now))
                .setPeriodStart(DateTimeUtils.timestamp(now.minusHours(1)))
                .build();
    }
}