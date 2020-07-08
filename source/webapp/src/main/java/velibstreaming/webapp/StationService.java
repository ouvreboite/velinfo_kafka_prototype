package velibstreaming.webapp;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import velibstreaming.avro.record.stream.AvroStationChange;
import velibstreaming.properties.StreamProperties;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class StationService {
    @Autowired
    private StreamProperties streamProperties;
    private final Map<String, AvroStationChange> availabilities = new ConcurrentHashMap<>();

    public List<AvroStationChange> getStations(){
        return availabilities.values().stream()
                .sorted(Comparator.comparing(station -> station.getStationName().toString()))
                .collect(Collectors.toList());
    }

    @KafkaListener(topics = "#{streamProperties.getStationChangesWithStaleTimestampTopic()}", groupId = "webapp.${random.uuid}", properties = {"auto.offset.reset = earliest"})
    public void consume(ConsumerRecord<String, AvroStationChange> record) {
        this.availabilities.put(record.key(), record.value());
    }
}
