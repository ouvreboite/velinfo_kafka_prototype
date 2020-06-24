package mapper;

import org.junit.jupiter.api.Test;
import velibstreaming.kafka.producer.mapper.RoadWorkMapper;
import velibstreaming.opendata.client.OpenDataClient;
import velibstreaming.opendata.client.RoadWorkClient;

import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RoadWorkMapperTest {
    @Test
    public void map_shouldMapToAvroRecord() throws OpenDataClient.OpenDataException {
        var dtos = new RoadWorkClient().get();
        var mapper = new RoadWorkMapper();

        var avroRecords = dtos.getRecords().stream()
                .map(r -> mapper.map(r.getFields()))
                .collect(Collectors.toList());

        assertEquals(dtos.getRecords().size(), avroRecords.size());
    }
}