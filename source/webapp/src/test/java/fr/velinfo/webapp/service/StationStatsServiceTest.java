package fr.velinfo.webapp.service;

import fr.velinfo.avro.record.stream.AvroStationStats;
import fr.velinfo.repository.HourlyStationStatsRepository;
import fr.velinfo.repository.Repository;
import fr.velinfo.webapp.dto.StationStat;
import fr.velinfo.webapp.dto.mapper.StationStatMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StationStatsServiceTest {

    @Mock
    private HourlyStationStatsRepository repository;

    private StationStatsService service;

    @BeforeEach
    void init(){
        this.service = new StationStatsService(new StationStatMapper(), repository);
    }

    @Test
    void getHourlyStatistics_shouldLoadFromRepositoryAndMapToDto() throws Repository.RepositoryException {
        when(repository.getStatsForPastDays("a", 30)).thenReturn(Arrays.asList(stats("a")));

        List<StationStat> stats = service.getHourlyStatistics("a", 30);

        verify(repository, times(1)).getStatsForPastDays("a", 30);
        assertEquals(1, stats.size());
        assertEquals("a", stats.get(0).getStationCode());
    }

    private AvroStationStats stats(String stationCode){
        return AvroStationStats.newBuilder()
                .setStationCode(stationCode)
                .setLastLoadTimestamp(0)
                .setLastNumberOfElectricBikes(0)
                .setLastNumberOfMechanicalBikes(0)
                .setMinimumNumberOfElectricBikes(0)
                .setMinimumNumberOfEmptySlots(0)
                .setMinimumNumberOfMechanicalBikes(0)
                .setNumberOfElectricBikesRented(0)
                .setNumberOfElectricBikesReturned(0)
                .setNumberOfMechanicalBikesRented(0)
                .setNumberOfMechanicalBikesReturned(0)
                .setPeriodEnd(0L)
                .setPeriodStart(0L)
                .build();
    }
}