package fr.velinfo.webapp.controller;

import fr.velinfo.repository.Repository;
import fr.velinfo.webapp.dto.Station;
import fr.velinfo.webapp.dto.StationStat;
import fr.velinfo.webapp.service.StationService;
import fr.velinfo.webapp.service.StationStatsService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController()
@RequestMapping("/api/stations")
public class StationRestController {

    private final StationService stationService;
    private final StationStatsService stationStatsService;

    public StationRestController(StationService stationService, StationStatsService stationStatsService) {
        this.stationService = stationService;
        this.stationStatsService = stationStatsService;
    }

    @GetMapping("/")
    @Operation(summary = "Get the current state of all stations", tags = "stations")
    public List<Station> getAllStations() {
        return stationService.getCurrentStationStates();
    }

    @GetMapping("/{id}/hourly-stats")
    @Operation(summary = "Get hourly statistics of a station for the past 30 days", tags = "statistics")
    public List<StationStat> getHourlyStatistics(@PathVariable("id") String stationCode) throws Repository.RepositoryException {
        return stationStatsService.getHourlyStatistics(stationCode, 30);
    }
}
