package velibstreaming.webapp;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import velibstreaming.avro.record.stream.AvroStation;
import velibstreaming.properties.StreamProperties;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class StationService {
    @Autowired
    private StreamProperties streamProperties;
    private final Map<String, AvroStation> availabilities = new ConcurrentHashMap<>();

    public List<AvroStation> getStations(){
        return availabilities.values().stream()
                .sorted(Comparator.comparing(station -> station.getStationName().toString()))
                .collect(Collectors.toList());
    }

    @KafkaListener(topics = "#{streamProperties.getStreamStationAvailabilityTopic()}", groupId = "webapp", properties = {"auto.offset.reset = earliest"})
    public void consume(ConsumerRecord<String, AvroStation> record) {
        this.availabilities.put(record.key(), record.value());
    }
}
