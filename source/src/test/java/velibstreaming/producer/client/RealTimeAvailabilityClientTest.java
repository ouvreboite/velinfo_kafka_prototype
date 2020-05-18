package velibstreaming.producer.client;

import velibstreaming.producer.client.dto.RealTimeAvailability;

import java.time.Instant;
import java.time.Period;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RealTimeAvailabilityClientTest {

    @org.junit.jupiter.api.Test
    void fetch_shouldFetchAvailabilitiesFromAPI_AndMapToDto() {
        RealTimeAvailability availabilities = new RealTimeAvailabilityClient().fetch();
        assertFalse(availabilities.getRecords().isEmpty());

        long totalCapacity = availabilities.getRecords().stream()
                .mapToLong(r -> r.getFields().getCapacity())
                .sum();
        assertTrue(totalCapacity > 1_000);
    }

    @org.junit.jupiter.api.Test
    void fetch_shouldFetchRecentData() {
        RealTimeAvailability availabilities = new RealTimeAvailabilityClient().fetch();

        Date mostRecentDate = availabilities.getRecords().stream().map(r -> r.getFields().getDuedate()).max(Date::compareTo).orElseThrow();
        Instant oneDayAgo = Instant.now().minus(Period.ofDays(1));
        assertTrue(mostRecentDate.toInstant().isAfter(oneDayAgo));
    }
}