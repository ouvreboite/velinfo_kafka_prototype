package velibstreaming.kafka.stream.builder.projection;

import org.junit.jupiter.api.Test;
import velibstreaming.avro.record.source.AvroCoordinates;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GeoProjectorTest {

    @Test
    void get100MeterZone_shouldRoundToThe3thDecimal() {
        String zone = new GeoProjector().get100MeterZone(new AvroCoordinates(45.0022, -10.0039));
        assertEquals("45,002_-10,004",zone);
    }

    @Test
    void get100MetersNearbyZones_shouldReturnItsZoneAndThe9AroundIt() {
        List<String> nearbyZones = new GeoProjector().get100MetersNearbyZones(new AvroCoordinates(45.0022, -10.0039));

        boolean allNearbyZonesPresent = nearbyZones.containsAll(Arrays.asList(
                "45,002_-10,004",

                "45,001_-10,003",
                "45,001_-10,004",
                "45,001_-10,005",

                "45,002_-10,003",
                "45,002_-10,004",
                "45,002_-10,005",

                "45,003_-10,003",
                "45,003_-10,004",
                "45,003_-10,005"
        ));

        assertEquals(10, nearbyZones.size());
        assertTrue(allNearbyZonesPresent);
    }
}