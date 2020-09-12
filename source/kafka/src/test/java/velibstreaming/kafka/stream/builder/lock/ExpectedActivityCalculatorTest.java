package velibstreaming.kafka.stream.builder.lock;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import velibstreaming.avro.record.stream.AvroStationStats;
import velibstreaming.properties.DateTimeUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ExpectedActivityCalculatorTest {
    private final ExpectedActivityCalculator calculator = new ExpectedActivityCalculator();
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").withZone(DateTimeUtils.ZONE_ID);


    @ParameterizedTest
    @MethodSource("expectedActivityProvider")
    void computeExpectedActivityOnSamePeriod(String start, String end, int expectedActivity, String description) throws IOException {
        LocalDateTime startPeriod = LocalDateTime.parse(start, formatter);
        LocalDateTime endPeriod = LocalDateTime.parse(end, formatter);

        int computedActivity = calculator.computeExpectedActivityOnSamePeriod(stats(), startPeriod, endPeriod);
        assertEquals(expectedActivity, computedActivity, description);
    }

    private static Stream<Arguments> expectedActivityProvider() {
        return Stream.of(
                Arguments.of("2020-07-28 01:00", "2020-07-28 01:00", 0, "start and end are equals, no activity is expected"),
                Arguments.of("2020-07-28 03:00", "2020-07-28 04:00", 14, "start and end represent the 3AM-4AM period for the day 07, 14 and 21, the result should be the median value"),
                Arguments.of("2020-07-28 03:00", "2020-07-28 03:45", 10, "end is not an exact hour, period should be partially accounted"),
                Arguments.of("2020-07-28 03:15", "2020-07-28 04:00", 10, "start is not an exact hour, period should be partially accounted"),
                Arguments.of("2020-07-28 03:15", "2020-07-28 03:45", 7, "start and end are not an exact hour, period should be partially accounted"),
                Arguments.of("2020-07-28 03:00", "2020-07-28 06:00", 3*14, "start and end represent the 3AM-6AM period, the result should be the median value"),
                Arguments.of("2020-07-27 04:00", "2020-07-28 02:00", 20*13+2*14, "start and end represent the 4AM-2AM (20 hours before midnight and 2 hours after) period, the result should be the median value"),
                Arguments.of("2020-07-27 04:15", "2020-07-28 01:45", 280, "start and end represent inexact hour (across midnight) period, the result should be the median value")
        );
    }

    private List<AvroStationStats> stats() throws IOException {
        ArrayList<AvroStationStats> stats = new ArrayList<>();
        File file = new File("src/test/resources/stats.csv");
        System.out.println(file.getAbsolutePath()+" "+file.exists());
        try (BufferedReader br = new BufferedReader(new FileReader("src/test/resources/stats.csv"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(";");
                stats.add(stat(values[0],Integer.parseInt(values[1])));
            }
        }

        return stats;
    }

    private AvroStationStats stat(String dateString, int activity) {
        LocalDateTime dateTime = LocalDateTime.parse(dateString, formatter);

        AvroStationStats stat = new AvroStationStats();
        stat.setPeriodStart(DateTimeUtils.timestamp(dateTime));
        stat.setPeriodEnd(DateTimeUtils.timestamp(dateTime.plusHours(1)));
        stat.setNumberOfElectricBikesRented(activity);
        return stat;
    }
}