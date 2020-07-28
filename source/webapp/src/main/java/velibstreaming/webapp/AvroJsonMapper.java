package velibstreaming.webapp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import velibstreaming.avro.record.stream.AvroStationUpdate;

import java.util.List;

public class AvroJsonMapper {
    private final ObjectMapper objectMapper;

    public AvroJsonMapper(){
        this.objectMapper = new ObjectMapper();
        this.objectMapper.addMixIn(Object.class, AvroMixIn.class);
    }

    public String serializeStations(List<AvroStationUpdate> stations) throws JsonProcessingException {
        return objectMapper.writeValueAsString(stations);
    }

    public interface AvroMixIn {
        @JsonIgnore
        org.apache.avro.Schema getSchema();
        @JsonIgnore
        org.apache.avro.specific.SpecificData getSpecificData();
    }
}
