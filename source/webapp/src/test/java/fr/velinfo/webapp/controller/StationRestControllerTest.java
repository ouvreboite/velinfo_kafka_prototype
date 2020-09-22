package fr.velinfo.webapp.controller;

import fr.velinfo.repository.Repository;
import fr.velinfo.webapp.dto.Station;
import fr.velinfo.webapp.dto.StationStat;
import fr.velinfo.webapp.service.StationService;
import fr.velinfo.webapp.service.StationStatsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class StationRestControllerTest {
    @Mock
    private StationService stationService;
    @Mock
    private StationStatsService stationStatsService;
    private StationRestController controller;

    @BeforeEach
    void init(){
        controller = new StationRestController(stationService, stationStatsService);
    }

    @Test
    void getAllStations_shouldUseStationService() {
        when(stationService.getCurrentStationStates()).thenReturn(Arrays.asList(new Station()));

        List<Station> allStations = controller.getAllStations();
        assertEquals(1,allStations.size());
        verify(stationService, times(1)).getCurrentStationStates();
    }

    @Test
    void getHourlyStatistics_shouldUseStationStatsService() throws Repository.RepositoryException {
        when(stationStatsService.getHourlyStatistics("a", 30)).thenReturn(Arrays.asList(new StationStat()));

        List<StationStat> hourlyStatistics = controller.getHourlyStatistics("a");
        assertEquals(1,hourlyStatistics.size());
        verify(stationStatsService, times(1)).getHourlyStatistics("a", 30);
    }
}