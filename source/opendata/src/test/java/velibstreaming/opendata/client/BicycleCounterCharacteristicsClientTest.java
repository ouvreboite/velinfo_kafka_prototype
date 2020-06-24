package velibstreaming.opendata.client;

import org.junit.jupiter.api.Test;
import velibstreaming.opendata.dto.BicycleCounterCharacteristics;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BicycleCounterCharacteristicsClientTest {
    private BicycleCounterCharacteristicsClient client = new BicycleCounterCharacteristicsClient();

    @Test
    void get_shouldFetchStationCharacteristicsFromAPI_AndMapToDto() throws OpenDataClient.OpenDataException {
        BicycleCounterCharacteristics characteristics = client.get();
        assertFalse(characteristics.getRecords().isEmpty(), "The API should return several counters");

        List<double[]> coordinatesWithoutTwoValues = characteristics.getRecords().stream()
                .map(r -> r.getFields().getCoordinates())
                .filter(coord -> coord.length != 2)
                .collect(Collectors.toList());
        assertTrue(coordinatesWithoutTwoValues.isEmpty(), "The geocoordinates returned by the API should be a tuple of double");
    }
}