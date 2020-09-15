package fr.velinfo.webapp;

import fr.velinfo.avro.record.stream.AvroStationUpdate;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class StationService {
    private final Map<String, AvroStationUpdate> availabilities = new ConcurrentHashMap<>();

    public List<AvroStationUpdate> getStations(){
        return availabilities.values().stream()
                .sorted(Comparator.comparing(station -> station.getStationName()))
                .collect(Collectors.toList());
    }

    @KafkaListener(topics = "#{T(fr.velinfo.properties.Topics).STATION_UPDATES}", groupId = "webapp.${random.uuid}", properties = {"auto.offset.reset = earliest"})
    public void consume(ConsumerRecord<String, AvroStationUpdate> record) {
        this.availabilities.put(record.key(), record.value());
    }
}
