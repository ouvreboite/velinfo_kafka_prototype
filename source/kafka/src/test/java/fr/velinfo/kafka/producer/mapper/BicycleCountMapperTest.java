package fr.velinfo.kafka.producer.mapper;

import org.junit.jupiter.api.Test;
import fr.velinfo.opendata.client.BicycleCountClient;
import fr.velinfo.opendata.client.OpenDataClient;

import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BicycleCountMapperTest {

    @Test
    public void map_shouldMapToAvroRecord() throws OpenDataClient.OpenDataException {
        var dtos = new BicycleCountClient().get();
        var mapper = new BicycleCountMapper();

        var avroRecords = dtos.getRecords().stream()
                .map(r -> mapper.map(r.getFields()))
                .collect(Collectors.toList());

        assertEquals(dtos.getRecords().size(), avroRecords.size());
    }
}