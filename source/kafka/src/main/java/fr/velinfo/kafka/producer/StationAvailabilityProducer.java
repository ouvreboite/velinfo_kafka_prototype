package fr.velinfo.kafka.producer;

import fr.velinfo.avro.record.source.AvroStationAvailability;
import fr.velinfo.kafka.producer.mapper.RealTimeAvailabilityMapper;
import fr.velinfo.opendata.dto.RealTimeAvailability;
import fr.velinfo.common.Topics;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Component
public class StationAvailabilityProducer {
    private static final Logger LOGGER = LoggerFactory.getLogger(StationAvailabilityProducer.class);
    private final KafkaProducer<String, AvroStationAvailability> kafkaProducer;
    private final RealTimeAvailabilityMapper avroMapper;

    public StationAvailabilityProducer(RealTimeAvailabilityMapper avroMapper, KafkaProducerBuilder kafkaProducerBuilder) {
        this.avroMapper = avroMapper;
        this.kafkaProducer = kafkaProducerBuilder.createProducer();
    }

    public void send(RealTimeAvailability payload) {
        try{
            List<ProducerRecord<String, AvroStationAvailability>> kafkaRecords = payload.getRecords().stream()
                    .map(record -> avroMapper.map(record.getFields()))
                    .map(avroAvailability -> new ProducerRecord<>(
                            Topics.STATION_AVAILABILITIES,
                            null,
                            avroAvailability.getLoadTimestamp(),
                            avroAvailability.getStationCode(),
                            avroAvailability))
                    .collect(Collectors.toList());

            for(var kafkaRecord : kafkaRecords){
                kafkaProducer.send(kafkaRecord).get();
            }

        }catch(RuntimeException | InterruptedException | ExecutionException e){
            LOGGER.error("Error pushing to Kafka",e);
        }

        kafkaProducer.flush();
    }
}
