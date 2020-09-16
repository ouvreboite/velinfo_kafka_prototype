package fr.velinfo.kafka.stream.builder.lock;

import fr.velinfo.avro.record.stream.AvroStationStats;
import fr.velinfo.avro.record.stream.AvroStationUpdate;
import fr.velinfo.properties.DateTimeUtils;
import fr.velinfo.repository.HourlyStationStatsRepository;
import fr.velinfo.repository.Repository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collection;

public class LockedStationDetector {

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

            System.out.println("Loading stats for station "+station.getStationCode()+" stale for "+lastChange.until(load, ChronoUnit.MINUTES)+" minutes");
            Collection<AvroStationStats> stats = hourlyStationStatsRepository.getStatsForPastDays(station.getStationCode(), 30);
            System.out.println(stats.size()+" stats loaded for station "+station.getStationCode());
            int expectedActivityOnSamePeriod = activityCalculator.computeExpectedActivityOnSamePeriod(stats, lastChange, load);
            System.out.println("Expected activity for station "+station.getStationCode()+" : "+expectedActivityOnSamePeriod);
            return expectedActivityOnSamePeriod > 100;
        }catch(Repository.RepositoryException e){
            System.out.println(e);
            e.printStackTrace();
            return false;
        }
    }

}
