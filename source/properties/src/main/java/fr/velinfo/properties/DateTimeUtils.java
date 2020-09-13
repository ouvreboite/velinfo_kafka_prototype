package fr.velinfo.properties;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class DateTimeUtils {
    public static final ZoneId ZONE_ID = ZoneId.of("Europe/Paris");
    public static LocalDateTime localDateTime(long timestamp) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), DateTimeUtils.ZONE_ID);
    }

    public static long timestamp(LocalDateTime localDateTime) {
        return Timestamp.valueOf(localDateTime).toInstant().toEpochMilli();
    }
}
