package mapper;

import org.junit.jupiter.api.Test;
import velibstreaming.kafka.producer.mapper.BicycleCounterCharacteristicsMapper;
import velibstreaming.opendata.client.BicycleCounterCharacteristicsClient;
import velibstreaming.opendata.client.OpenDataClient;

import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BicycleCounterCharacteristicsMapperTest {

    @Test
    public void map_shouldMapToAvroRecord() throws OpenDataClient.OpenDataException {
        var dtos = new BicycleCounterCharacteristicsClient().get();
        var mapper = new BicycleCounterCharacteristicsMapper();

        var avroRecords = dtos.getRecords().stream()
                .map(r -> mapper.map(r.getFields()))
                .collect(Collectors.toList());

        assertEquals(dtos.getRecords().size(), avroRecords.size());
    }
}