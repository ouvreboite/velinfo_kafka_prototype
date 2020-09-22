package fr.velinfo.webapp.dto.mapper;

import fr.velinfo.avro.record.stream.AvroStationStats;
import fr.velinfo.common.DateTimeUtils;
import fr.velinfo.webapp.dto.StationStat;
import org.springframework.stereotype.Service;

@Service
public class StationStatMapper {

    public StationStat map(AvroStationStats stationStats){
        StationStat stat = new StationStat();
        stat.setStationCode(stationStats.getStationCode());
        stat.setElectricBikesRented(stationStats.getNumberOfElectricBikesRented());
        stat.setElectricBikesReturned(stationStats.getNumberOfElectricBikesReturned());
        stat.setMechanicalBikesRented(stationStats.getNumberOfMechanicalBikesRented());
        stat.setMechanicalBikesReturned(stationStats.getNumberOfMechanicalBikesReturned());
        stat.setMinimumElectricBikes(stationStats.getMinimumNumberOfElectricBikes());
        stat.setMinimumMechanicalBikes(stationStats.getMinimumNumberOfMechanicalBikes());
        stat.setMinimumEmptySlots(stationStats.getMinimumNumberOfEmptySlots());
        stat.setPeriodStart(DateTimeUtils.localDateTime(stationStats.getPeriodStart()));
        stat.setPeriodEnd(DateTimeUtils.localDateTime(stationStats.getPeriodEnd()));
       return stat;
    }
}
