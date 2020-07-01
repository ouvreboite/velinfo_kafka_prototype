package velibstreaming.webapp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import velibstreaming.avro.record.stream.AvroStation;

import java.util.List;

public class AvroJsonMapper {
    private final ObjectMapper objectMapper;

    public AvroJsonMapper(){
        this.objectMapper = new ObjectMapper();
        this.objectMapper.addMixIn(Object.class, AvroMixIn.class);
    }

    public String serializeStations(List<AvroStation> stations) throws JsonProcessingException {
        return objectMapper.writeValueAsString(stations);
    }

    public interface AvroMixIn {
        @JsonSerialize(as=CharSequence.class)
        CharSequence getStationName();
        @JsonIgnore
        org.apache.avro.Schema getSchema();
        @JsonIgnore
        org.apache.avro.specific.SpecificData getSpecificData();
    }
}
