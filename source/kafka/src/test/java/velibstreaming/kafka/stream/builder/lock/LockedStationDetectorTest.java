package velibstreaming.kafka.stream.builder.lock;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import velibstreaming.avro.record.stream.AvroStationStats;
import velibstreaming.avro.record.stream.AvroStationUpdate;
import velibstreaming.properties.DateTimeUtils;
import velibstreaming.repository.HourlyStationStatsRepository;
import velibstreaming.repository.Repository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LockedStationDetectorTest {

    @Test
    public void isStationLocked_shouldReturnFalse_whenNoLastMovementTimestamp(@Mock HourlyStationStatsRepository repository, @Mock ExpectedActivityCalculator activityCalculator){
        var detector = new LockedStationDetector(repository, activityCalculator);

        Instant now = Instant.now();

        AvroStationUpdate station = new AvroStationUpdate();
        station.setLoadTimestamp(now.toEpochMilli());
        station.setLastMovementTimestamp(null);
        assertFalse(detector.isStationLocked(station), "When no lastMovementTimestamp is provided, station is not locked");

        station.setLastMovementTimestamp(now.toEpochMilli());
        assertFalse(detector.isStationLocked(station), "When lastMovementTimestamp is same as loadTimestamp, station is not locked");
    }

    @Test
    public void isStationLocked_shouldReturnTrue_whenLastMovementIsMoreThan12HoursOld(@Mock HourlyStationStatsRepository repository, @Mock ExpectedActivityCalculator activityCalculator){
        var detector = new LockedStationDetector(repository, activityCalculator);

        var now = LocalDateTime.now();

        AvroStationUpdate station = new AvroStationUpdate();
        station.setLoadTimestamp(DateTimeUtils.timestamp(now));
        station.setLastMovementTimestamp(DateTimeUtils.timestamp(now.minusHours(12)));
        assertTrue(detector.isStationLocked(station), "When lastMovementTimestamp is 12 hours old, station is locked");
    }

    @Test
    public void isStationLocked_dependsOnExpectedActivity_whenLastMovementIsLessThan12HoursOld_(@Mock HourlyStationStatsRepository repository, @Mock ExpectedActivityCalculator activityCalculator) throws Repository.RepositoryException {
        var detector = new LockedStationDetector(repository, activityCalculator);

        var now = DateTimeUtils.localDateTime(DateTimeUtils.timestamp(LocalDateTime.now()));
        // back and force between timestamp and LocalDateTime in the tested method strip the microsecond,
        // so we need to do it beforehand if we want to be able to match arguments in the mock
        var nowMinus10h = now.minusHours(10);

        AvroStationUpdate station = new AvroStationUpdate();
        station.setStationCode("ABC");
        station.setLoadTimestamp(DateTimeUtils.timestamp(now));
        station.setLastMovementTimestamp(DateTimeUtils.timestamp(nowMinus10h));

        var stats = new ArrayList<AvroStationStats>();
        when(repository.getStatsForPast30Days("ABC")).thenReturn(stats);
        when(activityCalculator.computeExpectedActivityOnSamePeriod(stats, nowMinus10h, now)).thenReturn(95);
        assertFalse(detector.isStationLocked(station), "When expected activity on same period is less than 100, station is not locked");

        when(activityCalculator.computeExpectedActivityOnSamePeriod(stats, nowMinus10h, now)).thenReturn(110);
        assertTrue(detector.isStationLocked(station), "When expected activity on same period is more than 100, station is locked");

    }
}