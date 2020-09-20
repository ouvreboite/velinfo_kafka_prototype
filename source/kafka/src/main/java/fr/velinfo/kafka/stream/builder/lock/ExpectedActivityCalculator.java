package fr.velinfo.kafka.stream.builder.lock;

import lombok.Data;
import fr.velinfo.avro.record.stream.AvroStationStats;
import fr.velinfo.kafka.utils.DateTimeUtils;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Component
public class ExpectedActivityCalculator {

    public int computeExpectedActivityOnSamePeriod(Collection<AvroStationStats> stats, LocalDateTime periodStart, LocalDateTime periodEnd){
        //filter stats on same hour
        Map<LocalDate, List<Activity>> activitiesByDay = stats.stream()
                .filter(filterCurrentStats(periodStart))
                .filter(keepOnlyStatsOnSamePeriod(periodStart, periodEnd))
                .map(ToActivity(periodStart, periodEnd))
                .collect(Collectors.groupingBy(Activity::getDay, toList()));

        int[] totalActivityPerDayOnPeriod = activitiesByDay.values()
                .stream()
                .mapToInt(activities -> activities.stream().mapToInt(a -> a.total).sum())
                .toArray();

        //compute median activity
        return median(totalActivityPerDayOnPeriod);
    }

    private Predicate<AvroStationStats> filterCurrentStats(LocalDateTime periodStart) {
        return stat -> DateTimeUtils.localDateTime(stat.getPeriodEnd()).isBefore(periodStart);
    }

    private int median(int[] array) {
        if(array.length == 0)
            return 0;
        Arrays.sort(array);
        return array[array.length/2];
    }

    private Predicate<AvroStationStats> keepOnlyStatsOnSamePeriod(LocalDateTime periodStart, LocalDateTime periodEnd) {
        return stat -> {
            LocalDateTime statStart = DateTimeUtils.localDateTime(stat.getPeriodStart());
            if(sameDay(periodStart, periodEnd)){
                return sameDay(periodStart, statStart) && periodStart.getHour() <= statStart.getHour() && statStart.getHour() <= periodEnd.getHour();
            }else{
                return (statStart.getHour() >= periodStart.getHour() && sameDay(statStart, periodStart))
                        || (statStart.getHour() <= periodEnd.getHour() && sameDay(statStart, periodEnd));
            }
        };
    }

    private boolean sameDay(LocalDateTime periodStart, LocalDateTime statStart) {
        return periodStart.getDayOfWeek() == statStart.getDayOfWeek();
    }

    @Data
    private static class Activity {
        int total;
        LocalDateTime start;
        LocalDateTime end;
        LocalDate day;
    }

    private Function<AvroStationStats, Activity> ToActivity(LocalDateTime periodStart, LocalDateTime periodEnd) {
        return stat -> {
            var activity = new Activity();
            activity.start = DateTimeUtils.localDateTime(stat.getPeriodStart());
            activity.end = DateTimeUtils.localDateTime(stat.getPeriodEnd());

            if(sameDay(periodStart, activity.getStart()))
                activity.day = activity.getStart().toLocalDate();
            if(sameDay(periodEnd, activity.getStart()))
                activity.day = activity.getStart().toLocalDate().minusDays(1);

            int missingMinutes = 0;
            if(sameDay(activity.getStart(), periodStart) && activity.start.getHour() == periodStart.getHour())
                missingMinutes += periodStart.getMinute();
            if(sameDay(activity.getStart(), periodEnd) && activity.start.getHour() == periodEnd.getHour())
                missingMinutes += (60-periodEnd.getMinute());

            var total = stat.getNumberOfElectricBikesRented() + stat.getNumberOfElectricBikesReturned() + stat.getNumberOfMechanicalBikesRented() + stat.getNumberOfElectricBikesReturned();
            activity.total = total * (60-missingMinutes) / 60;

            return activity;
        };
    }
}
