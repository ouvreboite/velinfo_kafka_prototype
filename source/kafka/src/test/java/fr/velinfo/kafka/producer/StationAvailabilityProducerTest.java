package fr.velinfo.kafka.producer;

import fr.velinfo.avro.record.source.AvroStationAvailability;
import fr.velinfo.kafka.producer.mapper.RealTimeAvailabilityMapper;
import fr.velinfo.opendata.dto.OpenDataDto;
import fr.velinfo.opendata.dto.RealTimeAvailability;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StationAvailabilityProducerTest {

    @Mock
    KafkaProducerBuilder producerBuilder;
    @Mock
    KafkaProducer<String, AvroStationAvailability> kafkaProducer;

    private StationAvailabilityProducer producer;

    @BeforeEach
    void init(){
        when(producerBuilder.<AvroStationAvailability>createProducer()).thenReturn(kafkaProducer);

        this.producer = new StationAvailabilityProducer( new RealTimeAvailabilityMapper(), producerBuilder);
    }

    @Test
    void send_shouldMapAndSendToKafka() {
        RealTimeAvailability availability = realTimeAvailability("A", "B");
        when(kafkaProducer.send(any(ProducerRecord.class))).thenReturn(CompletableFuture.completedFuture(null));

        this.producer.send(availability);
        verify(kafkaProducer, times(2)).send(any(ProducerRecord.class));
    }

    @Test
    void send_shouldCatchIfExceptionAreThrown() {
        RealTimeAvailability availability = realTimeAvailability("A", "B");
        when(kafkaProducer.send(any(ProducerRecord.class))).thenThrow(new RuntimeException("Exception"));

        this.producer.send(availability);
        verify(kafkaProducer, times(1)).send(any(ProducerRecord.class));
    }

    private RealTimeAvailability realTimeAvailability(String... stationCodes) {
        List<OpenDataDto.Record<RealTimeAvailability.Fields>> records = Arrays.stream(stationCodes)
                .map(code -> {
                    RealTimeAvailability.Fields fields = new RealTimeAvailability.Fields();
                    fields.setStationcode(code);
                    fields.setName("station" + code);
                    fields.setDuedate(new Date());
                    OpenDataDto.Record<RealTimeAvailability.Fields> record = new OpenDataDto.Record<>();
                    record.setFields(fields);
                    return record;
                }).collect(Collectors.toList());

        RealTimeAvailability availability = new RealTimeAvailability();
        availability.setRecords(records);
        return availability;
    }
}