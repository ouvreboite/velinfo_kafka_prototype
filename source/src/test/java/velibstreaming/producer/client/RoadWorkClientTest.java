package velibstreaming.producer.client;

import org.junit.jupiter.api.Test;
import velibstreaming.producer.client.dto.BicycleCount;
import velibstreaming.producer.client.dto.RoadWork;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static velibstreaming.producer.client.OpenDataClient.ROW_COUNT_PARAMETER;
import static velibstreaming.producer.client.OpenDataClient.ROW_COUNT_PARAMETER_MAX_VALUE;

class RoadWorkClientTest {
    private RoadWorkClient client = new RoadWorkClient();

    @Test
    void get_shouldFetchRoadWorksFromAPI_AndMapToDto() {
        RoadWork roadWork = client
                .withParameter(ROW_COUNT_PARAMETER, ROW_COUNT_PARAMETER_MAX_VALUE)
                .get();

        assertFalse(roadWork.getRecords().isEmpty(), "The API should return several road works");
        assertNotNull(roadWork.getRecords().get(0).getFields().getStatut(), "The status should be correctly deserialized");
        assertNotNull(roadWork.getRecords().get(0).getFields().getNiveau_perturbation(), "The perturbation level should be correctly deserialized");
    }
}