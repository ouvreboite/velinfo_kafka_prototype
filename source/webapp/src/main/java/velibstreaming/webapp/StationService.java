package velibstreaming.webapp;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import velibstreaming.avro.record.stream.AvroStationUpdate;
import velibstreaming.properties.StreamProperties;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class StationService {
    @Autowired
    private StreamProperties streamProperties;
    private final Map<String, AvroStationUpdate> availabilities = new ConcurrentHashMap<>();

    public List<AvroStationUpdate> getStations(){
        return availabilities.values().stream()
                .sorted(Comparator.comparing(station -> station.getStationName()))
                .collect(Collectors.toList());
    }

    @KafkaListener(topics = "#{streamProperties.getStationChangesWithStaleStatusTopic()}", groupId = "webapp.${random.uuid}", properties = {"auto.offset.reset = earliest"})
    public void consume(ConsumerRecord<String, AvroStationUpdate> record) {
        this.availabilities.put(record.key(), record.value());
    }
}
