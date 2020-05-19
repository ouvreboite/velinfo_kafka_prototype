package velibstreaming.producer.client;

import velibstreaming.producer.client.dto.RealTimeAvailability;

import java.time.Instant;
import java.time.Period;
import java.util.Date;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RealTimeAvailabilityClientTest {

    @Test
    void fetch_shouldFetchAvailabilitiesFromAPI_AndMapToDto() {
        RealTimeAvailability availabilities = new RealTimeAvailabilityClient().fetch();
        assertFalse(availabilities.getRecords().isEmpty(), "The API should return several availabilities");

        long totalMechanicalAvailable = availabilities.getRecords().stream()
                .mapToLong(r -> r.getFields().getMechanical())
                .sum();
        assertTrue(totalMechanicalAvailable > 100, "Their should be some mechanical bicycle available");
    }

    @Test
    void fetch_shouldFetchRecentData() {
        RealTimeAvailability availabilities = new RealTimeAvailabilityClient().fetch();

        Date mostRecentDate = availabilities.getRecords().stream().map(r -> r.getFields().getDuedate()).max(Date::compareTo).orElseThrow();
        Instant oneDayAgo = Instant.now().minus(Period.ofDays(1));
        assertTrue(mostRecentDate.toInstant().isAfter(oneDayAgo), "The availability data should be at max a few hours old");
    }
}