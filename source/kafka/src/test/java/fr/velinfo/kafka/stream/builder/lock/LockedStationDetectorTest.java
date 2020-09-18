package fr.velinfo.kafka.stream.builder.lock;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import fr.velinfo.avro.record.stream.AvroStationStats;
import fr.velinfo.avro.record.stream.AvroStationUpdate;
import fr.velinfo.kafka.utils.DateTimeUtils;
import fr.velinfo.repository.HourlyStationStatsRepository;
import fr.velinfo.repository.Repository;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LockedStationDetectorTest {

    @Test
    public void isStationLocked_shouldReturnFalse_whenNolastChangeTimestamp(@Mock HourlyStationStatsRepository repository, @Mock ExpectedActivityCalculator activityCalculator){
        var detector = new LockedStationDetector(repository, activityCalculator);

        var now = DateTimeUtils.now();

        AvroStationUpdate station = new AvroStationUpdate();
        station.setLoadTimestamp(DateTimeUtils.timestamp(now));
        station.setLastChangeTimestamp(null);
        assertFalse(detector.isStationLocked(station), "When no lastChangeTimestamp is provided, station is not locked");

        station.setLastChangeTimestamp(DateTimeUtils.timestamp(now));
        assertFalse(detector.isStationLocked(station), "When lastChangeTimestamp is same as loadTimestamp, station is not locked");
    }

    @Test
    public void isStationLocked_shouldReturnTrue_whenLastMovementIsMoreThan12HoursOld(@Mock HourlyStationStatsRepository repository, @Mock ExpectedActivityCalculator activityCalculator){
        var detector = new LockedStationDetector(repository, activityCalculator);

        var now = DateTimeUtils.now();

        AvroStationUpdate station = new AvroStationUpdate();
        station.setLoadTimestamp(DateTimeUtils.timestamp(now));
        station.setLastChangeTimestamp(DateTimeUtils.timestamp(now.minusHours(12)));
        assertTrue(detector.isStationLocked(station), "When lastChangeTimestamp is 12 hours old, station is locked");
    }

    @Test
    public void isStationLocked_dependsOnExpectedActivity_whenLastMovementIsLessThan12HoursOld_(@Mock HourlyStationStatsRepository repository, @Mock ExpectedActivityCalculator activityCalculator) throws Repository.RepositoryException {
        var detector = new LockedStationDetector(repository, activityCalculator);

        var now = DateTimeUtils.localDateTime(DateTimeUtils.timestamp(DateTimeUtils.now()));
        // back and force between timestamp and LocalDateTime in the tested method strip the microsecond,
        // so we need to do it beforehand if we want to be able to match arguments in the mock
        var nowMinus10h = now.minusHours(10);

        AvroStationUpdate station = new AvroStationUpdate();
        station.setStationCode("ABC");
        station.setLoadTimestamp(DateTimeUtils.timestamp(now));
        station.setLastChangeTimestamp(DateTimeUtils.timestamp(nowMinus10h));

        var stats = new ArrayList<AvroStationStats>();
        when(repository.getStatsForPastDays("ABC", 30)).thenReturn(stats);
        when(activityCalculator.computeExpectedActivityOnSamePeriod(stats, nowMinus10h, now)).thenReturn(95);
        assertFalse(detector.isStationLocked(station), "When expected activity on same period is less than 100, station is not locked");

        when(activityCalculator.computeExpectedActivityOnSamePeriod(stats, nowMinus10h, now)).thenReturn(110);
        assertTrue(detector.isStationLocked(station), "When expected activity on same period is more than 100, station is locked");

    }
}