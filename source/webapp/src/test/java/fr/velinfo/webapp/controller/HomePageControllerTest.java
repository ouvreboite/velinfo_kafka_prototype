package fr.velinfo.webapp.controller;

import fr.velinfo.webapp.dto.Station;
import fr.velinfo.webapp.service.StationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.ExtendedModelMap;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HomePageControllerTest {

    private HomePageController controller;
    @Mock
    private StationService service;

    @BeforeEach
    void setup(){
        controller = new HomePageController(service);
    }

    @Test
    void getStationList_shouldAddStationsToModel() {
        var stations = Arrays.asList(new Station());
        when(service.getCurrentStationStates()).thenReturn(stations);

        var extendedModelMap = new ExtendedModelMap();
        controller.getStationList(extendedModelMap);

        List<Station> modelStations = (List<Station>) extendedModelMap.getAttribute("stations");
        assertArrayEquals(stations.toArray(), modelStations.toArray());
    }

    @Test
    void getStationList_shouldReturnTemplateName() {
        when(service.getCurrentStationStates()).thenReturn(null);

        String templateName = controller.getStationList(new ExtendedModelMap());

        assertEquals("home-page", templateName);
    }
}