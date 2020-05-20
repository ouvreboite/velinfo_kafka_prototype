package velibstreaming.producer.client;

import org.junit.jupiter.api.Test;
import velibstreaming.producer.client.dto.RealTimeAvailability;

import java.time.Instant;
import java.time.Period;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static velibstreaming.producer.client.OpenDataClient.ROW_COUNT_PARAMETER;
import static velibstreaming.producer.client.OpenDataClient.ROW_COUNT_PARAMETER_MAX_VALUE;

class RealTimeAvailabilityClientTest {
    private RealTimeAvailabilityClient client = new RealTimeAvailabilityClient();

    @Test
    void get_shouldFetchAvailabilitiesFromAPI_AndMapToDto() {
        RealTimeAvailability availabilities = client
                .withParameter(ROW_COUNT_PARAMETER, ROW_COUNT_PARAMETER_MAX_VALUE)
                .get();
        assertFalse(availabilities.getRecords().isEmpty(), "The API should return several availabilities");

        long totalMechanicalAvailable = availabilities.getRecords().stream()
                .mapToLong(r -> r.getFields().getMechanical())
                .sum();
        assertTrue(totalMechanicalAvailable > 100, "Their should be some mechanical bicycle available");
    }

    @Test
    void get_shouldFetchRecentData() {
        RealTimeAvailability availabilities = client
                .get();

        Date mostRecentDate = availabilities.getRecords().stream().map(r -> r.getFields().getDuedate()).max(Date::compareTo).orElseThrow();
        Instant oneDayAgo = Instant.now().minus(Period.ofDays(1));
        assertTrue(mostRecentDate.toInstant().isAfter(oneDayAgo), "The availability data should be at max a few hours old");
    }
}