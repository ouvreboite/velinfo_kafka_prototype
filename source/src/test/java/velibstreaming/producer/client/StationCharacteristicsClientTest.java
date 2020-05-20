package velibstreaming.producer.client;

import org.junit.jupiter.api.Test;
import velibstreaming.producer.client.dto.StationCharacteristics;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StationCharacteristicsClientTest {
    private StationCharacteristicsClient client = new StationCharacteristicsClient();

    @Test
    void get_shouldFetchStationCharacteristicsFromAPI_AndMapToDto() {
        StationCharacteristics stations = client
                .get();
        assertFalse(stations.getRecords().isEmpty(), "The API should return several stations");

        List<double[]> coordinatesWithoutTwoValues = stations.getRecords().stream()
                .map(r -> r.getFields().getCoordonnees_geo())
                .filter(coord -> coord.length != 2)
                .collect(Collectors.toList());
        assertTrue(coordinatesWithoutTwoValues.isEmpty(), "The geocoordinates returned by the API should be a tuple of double");
    }
}