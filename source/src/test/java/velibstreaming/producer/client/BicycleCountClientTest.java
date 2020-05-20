package velibstreaming.producer.client;

import org.junit.jupiter.api.Test;
import velibstreaming.producer.client.dto.BicycleCount;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static velibstreaming.producer.client.OpenDataClient.ROW_COUNT_PARAMETER;
import static velibstreaming.producer.client.OpenDataClient.ROW_COUNT_PARAMETER_MAX_VALUE;

class BicycleCountClientTest {
    private BicycleCountClient client = new BicycleCountClient();

    @Test
    void get_shouldFetchBicycleCountsFromAPI_AndMapToDto() {
        BicycleCount bicycleCount = client
                .withParameter(ROW_COUNT_PARAMETER, ROW_COUNT_PARAMETER_MAX_VALUE)
                .get();

        assertFalse(bicycleCount.getRecords().isEmpty(), "The API should return several counts");
    }

    @Test
    void get_shouldUseDateParameter() {
        String yesterday = LocalDate.now().minusDays(1).format(DateTimeFormatter.ISO_DATE);
        BicycleCount bicycleCount = client
                .withParameter(BicycleCountClient.DATE_PARAMETER, yesterday)
                .get();

        assertFalse(bicycleCount.getRecords().isEmpty(), "The API should return several counts");

        SimpleDateFormat iso = new SimpleDateFormat("yyyy-MM-dd");
        List<String> datesReturned = bicycleCount.getRecords().stream()
                .map(r -> r.getFields().getDate())
                .map(d -> iso.format(d))
                .distinct()
                .collect(Collectors.toList());
        assertEquals(1, datesReturned.size(), "The API should return only one date");
        assertEquals(yesterday, datesReturned.get(0), "The API should return only yesterday's data");
    }
}