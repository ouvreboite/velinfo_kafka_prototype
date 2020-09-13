package fr.velinfo.opendata.client;

import org.junit.jupiter.api.Test;
import fr.velinfo.opendata.dto.RealTimeAvailability;

import java.time.Instant;
import java.time.Period;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class RealTimeAvailabilityClientTest {
    private RealTimeAvailabilityClient client = new RealTimeAvailabilityClient();

    @Test
    void get_shouldFetchAvailabilitiesFromAPI_AndMapToDto() throws OpenDataClient.OpenDataException {
        RealTimeAvailability availabilities = client
                .withParameter(OpenDataClient.ROW_COUNT_PARAMETER, OpenDataClient.ROW_COUNT_PARAMETER_MAX_VALUE)
                .get();
        assertFalse(availabilities.getRecords().isEmpty(), "The API should return several availabilities");

        long totalMechanicalAvailable = availabilities.getRecords().stream()
                .mapToLong(r -> r.getFields().getMechanical())
                .sum();
        assertTrue(totalMechanicalAvailable > 100, "Their should be some mechanical bicycle available");

        List<double[]> coordinatesWithoutTwoValues = availabilities.getRecords().stream()
                .map(r -> r.getFields().getCoordonnees_geo())
                .filter(coord -> coord.length != 2)
                .collect(Collectors.toList());
        assertTrue(coordinatesWithoutTwoValues.isEmpty(), "The coordinates should be a two value double array");
    }

    @Test
    void get_shouldFetchRecentData() throws OpenDataClient.OpenDataException {
        RealTimeAvailability availabilities = client
                .get();

        Date mostRecentDate = availabilities.getRecords().stream().map(r -> r.getFields().getDuedate()).max(Date::compareTo).orElseThrow();
        Instant oneDayAgo = Instant.now().minus(Period.ofDays(1));
        assertTrue(mostRecentDate.toInstant().isAfter(oneDayAgo), "The availability data should be at max a few hours old");
    }
}