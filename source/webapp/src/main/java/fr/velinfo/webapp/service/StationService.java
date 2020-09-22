package fr.velinfo.webapp.service;

import fr.velinfo.avro.record.stream.AvroStationStatus;
import fr.velinfo.avro.record.stream.AvroStationUpdate;
import fr.velinfo.webapp.dto.Station;
import fr.velinfo.webapp.dto.mapper.StationMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class StationService {
    private StationMapper stationMapper;

    public StationService(StationMapper stationMapper) {
        this.stationMapper = stationMapper;
    }

    private final Map<String, Station> stations = new ConcurrentHashMap<>();

    public List<Station> getCurrentStationStates(){
        return stations.values().stream()
                .sorted(Comparator.comparing(station -> station.getStationName()))
                .collect(Collectors.toList());
    }

    @KafkaListener(topics = "#{T(fr.velinfo.common.Topics).STATION_UPDATES}", groupId = "webapp.${random.uuid}", properties = {"auto.offset.reset = earliest"})
    public void consumeStationUpdate(ConsumerRecord<String, AvroStationUpdate> record) {
        var station = this.stations.get(record.key());
        var prevStatus = station == null ?  null : station.getStatus();

        station = stationMapper.map(record.value());
        station.setStatus(prevStatus);

        this.stations.put(record.key(), station);
    }

    @KafkaListener(topics = "#{T(fr.velinfo.common.Topics).STATION_STATUS}", groupId = "webapp.${random.uuid}", properties = {"auto.offset.reset = earliest"})
    public void consumeStationStatus(ConsumerRecord<String, AvroStationStatus> record) {
        var station = this.stations.get(record.key());
        if(station == null) {
            station = new Station();
            station.setStationCode(record.key());
            this.stations.put(record.key(), station);
        }
        station.setStatus(record.value().getStatus().name());
    }
}
