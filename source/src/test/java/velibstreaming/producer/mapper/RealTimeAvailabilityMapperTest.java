package velibstreaming.producer.mapper;

import org.junit.jupiter.api.Test;
import velibstreaming.producer.client.OpenDataClient;
import velibstreaming.producer.client.RealTimeAvailabilityClient;

import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RealTimeAvailabilityMapperTest {
    @Test
    public void map_shouldMapToAvroRecord() throws OpenDataClient.OpenDataException {
        var dtos = new RealTimeAvailabilityClient().get();
        var mapper = new RealTimeAvailabilityMapper();

        var avroRecords = dtos.getRecords().stream()
                .map(r -> mapper.map(r.getFields()))
                .collect(Collectors.toList());

        assertEquals(dtos.getRecords().size(), avroRecords.size());
    }
}