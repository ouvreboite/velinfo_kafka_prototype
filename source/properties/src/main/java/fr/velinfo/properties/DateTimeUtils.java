package fr.velinfo.properties;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class DateTimeUtils {
    static ZoneId ZONE_ID = ZoneId.of("Europe/Paris");

    public static LocalDateTime now(){
        return LocalDateTime.now(DateTimeUtils.ZONE_ID);
    }
    public static LocalDateTime localDateTime(long timestamp) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), DateTimeUtils.ZONE_ID);
    }

    public static long timestamp(LocalDateTime localDateTime) {
        return localDateTime.atZone(ZONE_ID).toInstant().toEpochMilli();
    }
}
