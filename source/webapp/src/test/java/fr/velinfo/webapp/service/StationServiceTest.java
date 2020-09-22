package fr.velinfo.webapp.service;

import fr.velinfo.avro.record.source.AvroCoordinates;
import fr.velinfo.avro.record.stream.AvroStationStatus;
import fr.velinfo.avro.record.stream.AvroStationUpdate;
import fr.velinfo.avro.record.stream.StationStatus;
import fr.velinfo.webapp.dto.Station;
import fr.velinfo.webapp.dto.mapper.StationMapper;
import fr.velinfo.webapp.service.StationService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StationServiceTest {
    private final StationService service = new StationService(new StationMapper());
    @Test
    void getStations_shouldReturnEmpty_whenNoStationOrStatusConsumed() {
       assertTrue(service.getCurrentStationStates().isEmpty());
    }

    @Test
    void getStations_shouldReturnStationWithoutStatus_whenOnlyStationUpdateTopicPush() {
        service.consumeStationUpdate(new ConsumerRecord<>("topic", 0, 0, "a", update("StationA")));

        assertEquals(1,service.getCurrentStationStates().size());

        Station station = service.getCurrentStationStates().get(0);
        assertEquals("StationA", station.getStationName());
        assertNull(station.getStatus());
    }

    @Test
    void getStations_shouldReturnStationWithoutName_whenOnlyStationStatusTopicPush() {
        service.consumeStationStatus(new ConsumerRecord<>("topic", 0, 0, "a", status(StationStatus.LOCKED)));

        assertEquals(1,service.getCurrentStationStates().size());

        Station station = service.getCurrentStationStates().get(0);
        assertEquals("LOCKED", station.getStatus());
        assertNull(station.getStationName());
    }

    @Test
    void getStations_shouldMergeStationAndStatus_whenStationPushedFirst() {
        service.consumeStationUpdate(new ConsumerRecord<>("topic", 0, 0, "a", update("StationA")));
        service.consumeStationStatus(new ConsumerRecord<>("topic", 0, 0, "a", status(StationStatus.LOCKED)));

        assertEquals(1,service.getCurrentStationStates().size());

        Station station = service.getCurrentStationStates().get(0);
        assertEquals("StationA", station.getStationName());
        assertEquals("LOCKED", station.getStatus());
    }

    @Test
    void getStations_shouldMergeStationAndStatus_whenStatusPushedFirst() {
        service.consumeStationStatus(new ConsumerRecord<>("topic", 0, 0, "a", status(StationStatus.LOCKED)));
        service.consumeStationUpdate(new ConsumerRecord<>("topic", 0, 0, "a", update("StationA")));

        assertEquals(1,service.getCurrentStationStates().size());

        Station station = service.getCurrentStationStates().get(0);
        assertEquals("StationA", station.getStationName());
        assertEquals("LOCKED", station.getStatus());
    }


    private AvroStationUpdate update(String stationName) {
        return AvroStationUpdate.newBuilder()
                .setStationCode("A")
                .setStationName(stationName)
                .setStationCapacity(10)
                .setElectricBikesAtStation(2)
                .setMechanicalBikesAtStation(1)
                .setCoordinates(AvroCoordinates.newBuilder()
                        .setLatitude(11.1)
                        .setLongitude(22.2)
                        .build())
                .setLoadTimestamp(123L)
                .setLastChangeTimestamp(456L)
                .setMechanicalBikesRented(0)
                .setMechanicalBikesReturned(0)
                .setElectricBikesRented(0)
                .setElectricBikesReturned(0)
                .setAvailabilityTimestamp(0)
                .setIsRenting(true)
                .setIsReturning(true)
                .setIsInstalled(true)
                .build();
    }

    private AvroStationStatus status(StationStatus status) {
        return AvroStationStatus.newBuilder()
                .setStationCode("A")
                .setStatus(status)
                .setStaleSince(0L)
                .build();
    }
}