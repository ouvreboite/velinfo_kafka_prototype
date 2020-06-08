package velibstreaming.producer.mapper;

import org.junit.jupiter.api.Test;
import velibstreaming.producer.client.OpenDataClient;
import velibstreaming.producer.client.StationCharacteristicsClient;

import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StationCharacteristicsMapperTest {
    @Test
    public void map_shouldMapToAvroRecord() throws OpenDataClient.OpenDataException {
        var dtos = new StationCharacteristicsClient().get();
        var mapper = new StationCharacteristicsMapper();

        var avroRecords = dtos.getRecords().stream()
                .map(r -> mapper.map(r.getFields()))
                .collect(Collectors.toList());

        assertEquals(dtos.getRecords().size(), avroRecords.size());
    }
}