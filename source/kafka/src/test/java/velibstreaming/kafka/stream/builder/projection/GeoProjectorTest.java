package velibstreaming.kafka.stream.builder.projection;

import org.junit.jupiter.api.Test;
import velibstreaming.avro.record.source.AvroCoordinates;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GeoProjectorTest {

    @Test
    void get100MeterZone_shouldRoundBy0_002() {
        GeoProjector projector = new GeoProjector();
        assertEquals("45,002_-10,004", projector.get100MeterZone(new AvroCoordinates(45.0022, -10.0039)));
        assertEquals("45,002_-10,004", projector.get100MeterZone(new AvroCoordinates(45.0029, -10.0049)));
        assertEquals("45,004_-10,006", projector.get100MeterZone(new AvroCoordinates(45.0037, -10.0059)));
    }

    @Test
    void get100MetersNearbyZones_shouldReturnItsZoneAndThe9AroundIt() {
        List<String> nearbyZones = new GeoProjector().get100MetersNearbyZones(new AvroCoordinates(45.0022, -10.0039));

        boolean allNearbyZonesPresent = nearbyZones.containsAll(Arrays.asList(
                "45,000_-10,002",
                "45,000_-10,004",
                "45,000_-10,006",

                "45,002_-10,002",
                "45,002_-10,004",
                "45,002_-10,006",

                "45,004_-10,002",
                "45,004_-10,004",
                "45,004_-10,006"
        ));

        assertEquals(9, nearbyZones.size());
        assertTrue(allNearbyZonesPresent);
    }
}