package fr.velinfo.opendata.client;

import org.junit.jupiter.api.Test;
import fr.velinfo.opendata.dto.BicycleCount;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.TimeZone;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class BicycleCountClientTest {
    private BicycleCountClient client = new BicycleCountClient();

    @Test
    void get_shouldFetchBicycleCountsFromAPI_AndMapToDto() throws OpenDataClient.OpenDataException {
        BicycleCount bicycleCount = client
                .withParameter(OpenDataClient.ROW_COUNT_PARAMETER, OpenDataClient.ROW_COUNT_PARAMETER_MAX_VALUE)
                .get();

        assertFalse(bicycleCount.getRecords().isEmpty(), "The API should return several counts");

        List<double[]> coordinatesWithoutTwoValues = bicycleCount.getRecords().stream()
                .map(r -> r.getFields().getCoordinates())
                .filter(coord -> coord.length != 2)
                .collect(Collectors.toList());
        assertTrue(coordinatesWithoutTwoValues.isEmpty(), "The geocoordinates returned by the API should be a tuple of double");

    }

    @Test
    void get_shouldNotReachMaxSize() throws OpenDataClient.OpenDataException {
        String aWeekAgo = LocalDate.now().minusWeeks(1).format(DateTimeFormatter.ISO_DATE);
        BicycleCount bicycleCount = client
                .withParameter(OpenDataClient.ROW_COUNT_PARAMETER, OpenDataClient.ROW_COUNT_PARAMETER_MAX_VALUE)
                .withParameter(BicycleCountClient.DATE_PARAMETER, aWeekAgo)
                .get();

        assertTrue(bicycleCount.getRecords().size() < OpenDataClient.ROW_COUNT_PARAMETER_MAX_VALUE, "The API should not reach max size");
    }

    @Test
    void get_shouldUseDateParameter() throws OpenDataClient.OpenDataException {
        String aWeekAgo = LocalDate.now().minusWeeks(1).format(DateTimeFormatter.ISO_DATE);
        BicycleCount bicycleCount = client
                .withParameter(OpenDataClient.ROW_COUNT_PARAMETER, OpenDataClient.ROW_COUNT_PARAMETER_MAX_VALUE)
                .withParameter(BicycleCountClient.DATE_PARAMETER, aWeekAgo)
                .get();

        assertFalse(bicycleCount.getRecords().isEmpty(), "The API should return several counts");

        SimpleDateFormat iso = new SimpleDateFormat("yyyy-MM-dd");
        iso.setTimeZone(TimeZone.getTimeZone("UTC"));

        List<String> datesReturned = bicycleCount.getRecords().stream()
                .map(r -> r.getFields().getDate())
                .map(d -> iso.format(d))
                .distinct()
                .collect(Collectors.toList());
        assertEquals(1, datesReturned.size(), "The API should return only one date, but returned : "+datesReturned.toString());
        assertEquals(aWeekAgo, datesReturned.get(0), "The API should return only a week ago's data");
    }
}