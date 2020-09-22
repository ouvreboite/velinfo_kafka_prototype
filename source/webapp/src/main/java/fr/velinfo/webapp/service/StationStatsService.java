package fr.velinfo.webapp.service;

import fr.velinfo.avro.record.stream.AvroStationStats;
import fr.velinfo.repository.HourlyStationStatsRepository;
import fr.velinfo.repository.Repository;
import fr.velinfo.webapp.dto.StationStat;
import fr.velinfo.webapp.dto.mapper.StationStatMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StationStatsService {
    private static final Logger LOGGER = LoggerFactory.getLogger(StationStatsService.class);

    private final StationStatMapper mapper;
    private final HourlyStationStatsRepository statsRepository;

    public StationStatsService(StationStatMapper mapper, HourlyStationStatsRepository statsRepository) {
        this.mapper = mapper;
        this.statsRepository = statsRepository;
    }

    public List<StationStat> getHourlyStatistics(String stationCode, int nbDays) throws Repository.RepositoryException {
        LOGGER.info("Loading stats for {} for {} past days", stationCode, nbDays);
        Collection<AvroStationStats> statsForPastDays = statsRepository.getStatsForPastDays(stationCode, nbDays);
        LOGGER.info("{} stats loaded for {} for {} past days", statsForPastDays.size(), stationCode, nbDays);
        return statsForPastDays.stream()
                .map(mapper::map)
                .collect(Collectors.toList());
    }
}

