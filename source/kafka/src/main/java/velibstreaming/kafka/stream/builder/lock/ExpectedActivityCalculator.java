package velibstreaming.kafka.stream.builder.lock;

import lombok.Data;
import velibstreaming.avro.record.stream.AvroStationStats;
import velibstreaming.properties.DateTimeUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.summingInt;
import static java.util.stream.Collectors.toList;

public class ExpectedActivityCalculator {

    public int computeExpectedActivityOnSamePeriod(Collection<AvroStationStats> stats, LocalDateTime lastChange, LocalDateTime currentTime){
        //filter stats on same hour
        var periodStart = lastChange.toLocalTime();
        var periodEnd = currentTime.toLocalTime();

        Map<LocalDate, List<Activity>> activitiesByDay = stats.stream()
                .map(ToActivity(periodStart, periodEnd))
                .filter(keepOnlyStatsOnSamePeriod(periodStart, periodEnd))
                .collect(Collectors.groupingBy(stat -> stat.getStart().toLocalDate(), toList()));

        int[] totalActivityPerDayOnPeriod = activitiesByDay.values()
                .stream()
                .mapToInt(activities -> activities.stream().mapToInt(a -> a.total).sum())
                .toArray();

        //compute median activity
        if(totalActivityPerDayOnPeriod.length == 0)
            return 0;
        Arrays.sort(totalActivityPerDayOnPeriod);
        return totalActivityPerDayOnPeriod[totalActivityPerDayOnPeriod.length/2];
    }

    @Data
    private class Activity {
        int total;
        LocalDateTime start;
        LocalDateTime end;
    }

    private Function<AvroStationStats, Activity> ToActivity(LocalTime periodStart, LocalTime periodEnd) {
        return stat -> {
            var activity = new Activity();
            activity.start = DateTimeUtils.localDateTime(stat.getPeriodStart());
            activity.end = DateTimeUtils.localDateTime(stat.getPeriodEnd());

            int missingMinutes = 0;
            if(activity.start.getHour() == periodStart.getHour())
                missingMinutes += periodStart.getMinute();
            if(activity.start.getHour() == periodEnd.getHour())
                missingMinutes += (60-periodEnd.getMinute());

            var total = stat.getNumberOfElectricBikesRented() + stat.getNumberOfElectricBikesReturned() + stat.getNumberOfMechanicalBikesRented() + stat.getNumberOfElectricBikesReturned();
            activity.total = total * (60-missingMinutes) / 60;

            return activity;
        };
    }

    private Predicate<Activity> keepOnlyStatsOnSamePeriod(LocalTime periodStart, LocalTime periodEnd) {
        return activity -> {
            if(periodStart.equals(periodEnd) || periodStart.isBefore(periodEnd)){
                //standard case
                return activity.start.getHour() >= periodStart.getHour() && activity.start.getHour() <= periodEnd.getHour();
            }else{
                return activity.start.getHour() <= periodEnd.getHour() || activity.start.getHour() >= periodStart.getHour();
            }
        };
    }
}
