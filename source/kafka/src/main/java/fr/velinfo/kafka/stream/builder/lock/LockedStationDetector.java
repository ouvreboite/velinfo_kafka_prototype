package fr.velinfo.kafka.stream.builder.lock;

import fr.velinfo.avro.record.stream.AvroStationStats;
import fr.velinfo.avro.record.stream.AvroStationUpdate;
import fr.velinfo.common.DateTimeUtils;
import fr.velinfo.repository.HourlyStationStatsRepository;
import fr.velinfo.repository.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
@Component
public class LockedStationDetector {
    private static final Logger LOGGER = LoggerFactory.getLogger(LockedStationDetector.class);
    private final HourlyStationStatsRepository hourlyStationStatsRepository;
    private final ExpectedActivityCalculator activityCalculator;

    public LockedStationDetector(HourlyStationStatsRepository hourlyStationStatsRepository, ExpectedActivityCalculator activityCalculator) {
        this.hourlyStationStatsRepository = hourlyStationStatsRepository;
        this.activityCalculator = activityCalculator;
    }

    public boolean isStationLocked(AvroStationUpdate station){
        if(station.getLastChangeTimestamp() == null || station.getLastChangeTimestamp() == station.getLoadTimestamp())
            return false;

        LocalDateTime lastChange = DateTimeUtils.localDateTime(station.getLastChangeTimestamp());
        LocalDateTime load = DateTimeUtils.localDateTime(station.getLoadTimestamp());
        if(lastChange.until(load, ChronoUnit.HOURS) >= 12)
            return true;

        //get the past stats for station
        try{
            Collection<AvroStationStats> stats = hourlyStationStatsRepository.getStatsForPastDays(station.getStationCode(), 30);
            LOGGER.debug("{} stats loaded for station {}",stats.size(), station.getStationCode());
            int expectedActivityOnSamePeriod = activityCalculator.computeExpectedActivityOnSamePeriod(stats, lastChange, load);
            LOGGER.debug("Expected activity for station {} : {}",station.getStationCode(),expectedActivityOnSamePeriod);

            boolean isLocked = expectedActivityOnSamePeriod > 100;
            if(isLocked){
                LOGGER.info("Station {} is locked. Stale since {} minutes. Expected activity : {}",station.getStationCode(),lastChange.until(load, ChronoUnit.MINUTES), expectedActivityOnSamePeriod);
            }
            return isLocked;
        }catch(Repository.RepositoryException e){
            LOGGER.error("Error loading past stats for station :"+station.getStationCode(),e);
            return false;
        }
    }

}
