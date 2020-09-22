package fr.velinfo.common;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DateTimeUtilsTest {

    @Test
    public void localDateTimeAndTimestamp_shouldBeIdempotent(){
        var now = LocalDateTime.now().withNano(0);

        DateTimeUtils.ZONE_ID = ZoneId.of("Europe/Paris");
        var convertedNow = DateTimeUtils.localDateTime(DateTimeUtils.timestamp(now));
        assertEquals(now, convertedNow);

        DateTimeUtils.ZONE_ID = ZoneId.of("UTC");
        convertedNow = DateTimeUtils.localDateTime(DateTimeUtils.timestamp(now));
        assertEquals(now, convertedNow);
    }

}