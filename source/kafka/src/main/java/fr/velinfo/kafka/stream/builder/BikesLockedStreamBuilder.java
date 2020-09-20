package fr.velinfo.kafka.stream.builder;

import fr.velinfo.avro.record.stream.AvroBikesLocked;
import fr.velinfo.avro.record.stream.AvroStationStats;
import fr.velinfo.properties.ConnectionConfiguration;
import fr.velinfo.repository.HourlyStationStatsRepository;
import fr.velinfo.repository.Repository;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.function.ToLongFunction;
@Component
public class BikesLockedStreamBuilder {
    private final HourlyStationStatsRepository hourlyStationStatsRepository;

    public BikesLockedStreamBuilder(ConnectionConfiguration config) {
        this.hourlyStationStatsRepository = new HourlyStationStatsRepository(config);
    }

    public KStream<String, AvroBikesLocked> build(KStream<String, AvroStationStats> hourlyStatsStream){

        return hourlyStatsStream
                .map((s, stat) -> new KeyValue<>(s, toBikesLocked(stat)))
                .filter((s, avroBikesLocked) -> avroBikesLocked != null);
    }

    private AvroBikesLocked toBikesLocked(AvroStationStats stat) {
        try {
            Collection<AvroStationStats> stats = hourlyStationStatsRepository.getStatsForPastDays(stat.getStationCode(), 2);
            stats.add(stat);

            int lockedElectric = minimum(stats, AvroStationStats::getMinimumNumberOfElectricBikes).intValue();
            int lockedMechanical = minimum(stats, AvroStationStats::getMinimumNumberOfMechanicalBikes).intValue();
            int lockedSlots = minimum(stats, AvroStationStats::getMinimumNumberOfEmptySlots).intValue();
            long periodStart = minimum(stats, AvroStationStats::getPeriodStart);

            return AvroBikesLocked.newBuilder()
                    .setStationCode(stat.getStationCode())
                    .setElectricBikesLocked(lockedElectric)
                    .setMechanicalBikesLocked(lockedMechanical)
                    .setEmptySlotsLocked(lockedSlots)
                    .setPeriodStart(periodStart)
                    .setPeriodEnd(stat.getPeriodEnd())
                    .build();
        } catch (Repository.RepositoryException e) {
            e.printStackTrace();
            return null;
        }
    }

    private Long minimum(Collection<AvroStationStats> stats, ToLongFunction<AvroStationStats> longExtractor) {
        return stats.stream().mapToLong(longExtractor).min().orElse(0);
    }
}
