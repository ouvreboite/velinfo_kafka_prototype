package fr.velinfo.webapp.dto.mapper;

import fr.velinfo.avro.record.stream.AvroStationUpdate;
import fr.velinfo.webapp.dto.Station;
import org.springframework.stereotype.Service;

@Service
public class StationMapper {

    public Station map(AvroStationUpdate stationUpdate){
        Station station = new Station();
        station.setStationCode(stationUpdate.getStationCode());
        station.setStationName(stationUpdate.getStationName());
        station.setLongitude(stationUpdate.getCoordinates().getLongitude());
        station.setLatitude(stationUpdate.getCoordinates().getLatitude());
        station.setTotalCapacity(stationUpdate.getStationCapacity());
        station.setEmptySlots(stationUpdate.getStationCapacity()-(stationUpdate.getElectricBikesAtStation()+stationUpdate.getMechanicalBikesAtStation()));
        station.setElectricBikes(stationUpdate.getElectricBikesAtStation());
        station.setMechanicalBikes(stationUpdate.getMechanicalBikesAtStation());
        station.setLastChangeTimestamp(stationUpdate.getLastChangeTimestamp() == null ? stationUpdate.getLoadTimestamp() : stationUpdate.getLastChangeTimestamp());
        return station;
    }
}
