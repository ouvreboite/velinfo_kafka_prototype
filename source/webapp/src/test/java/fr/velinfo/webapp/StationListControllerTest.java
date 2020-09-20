package fr.velinfo.webapp;

import fr.velinfo.webapp.dto.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StationListControllerTest {

    private StationListController controller;
    @Mock
    private StationService service;

    @BeforeEach
    void setup(){
        controller = new StationListController(service);
    }

    @Test
    void getStationList_shouldAddStationsToModel() {
        var stations = Arrays.asList(new Station());
        when(service.getStations()).thenReturn(stations);

        var extendedModelMap = new ExtendedModelMap();
        controller.getStationList(extendedModelMap);

        List<Station> modelStations = (List<Station>) extendedModelMap.getAttribute("stations");
        assertArrayEquals(stations.toArray(), modelStations.toArray());
    }

    @Test
    void getStationList_shouldReturnTemplateName() {
        when(service.getStations()).thenReturn(null);

        String templateName = controller.getStationList(new ExtendedModelMap());

        assertEquals("station-list", templateName);
    }
}