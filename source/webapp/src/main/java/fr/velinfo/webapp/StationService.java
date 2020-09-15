package fr.velinfo.webapp;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import fr.velinfo.avro.record.stream.AvroStationUpdate;
import fr.velinfo.properties.ConnectionConfiguration;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class StationService {
    @Autowired
    private ConnectionConfiguration streamProperties;
    private final Map<String, AvroStationUpdate> availabilities = new ConcurrentHashMap<>();

    public List<AvroStationUpdate> getStations(){
        return availabilities.values().stream()
                .sorted(Comparator.comparing(station -> station.getStationName()))
                .collect(Collectors.toList());
    }

    @KafkaListener(topics = "#{streamProperties.getStationUpdatesTopic()}", groupId = "webapp.${random.uuid}", properties = {"auto.offset.reset = earliest"})
    public void consume(ConsumerRecord<String, AvroStationUpdate> record) {
        this.availabilities.put(record.key(), record.value());
    }
}
