package fr.velinfo.kafka.stream.builder.deduplication;

import fr.velinfo.kafka.stream.builder.StreamTestUtils;
import fr.velinfo.properties.ConnectionConfiguration;
import io.confluent.kafka.schemaregistry.testutil.MockSchemaRegistry;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.processor.MockProcessorContext;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.Stores;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import fr.velinfo.avro.record.source.AvroCoordinates;
import fr.velinfo.avro.record.stream.AvroStationUpdate;
import fr.velinfo.kafka.stream.AvroSerdeBuilder;
import fr.velinfo.kafka.utils.DateTimeUtils;

import java.time.LocalDateTime;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

class StationUpdateDeduplicatorTest {
    private final StationUpdateDeduplicator deduplicator = new StationUpdateDeduplicator("store");

    @BeforeEach
    public void initStore() {
        Properties properties = new Properties();
        properties.setProperty("schema.registry.url", StreamTestUtils.getSchemaRegistryUrl());
        ConnectionConfiguration connectionConfiguration = new ConnectionConfiguration(properties);
        AvroSerdeBuilder avroSerdeBuilder = new AvroSerdeBuilder(connectionConfiguration);

        var context = new MockProcessorContext();
        final KeyValueStore<String, AvroStationUpdate> store =
                Stores.keyValueStoreBuilder(
                        Stores.inMemoryKeyValueStore("store"),
                        Serdes.String(),
                        avroSerdeBuilder.<AvroStationUpdate>avroSerde()
                )
                .withLoggingDisabled() // Changelog is not supported by MockProcessorContext
                .build();

        store.init(context, store);
        context.register(store, /*parameter unused in mock*/ null);
        deduplicator.init(context);
    }

    @AfterEach
    public void tearDown() {
        MockSchemaRegistry.dropScope(StreamTestUtils.getSchemaRegistryUrl());
    }

    @Test
    public void deduplicator_shouldLetThrough_whenFirstOccurrenceOfStation(){
        var newStation = station("a", 1, 2, true, DateTimeUtils.timestamp(DateTimeUtils.now()));
        var output = deduplicator.transform("a", newStation);

        assertEquals(newStation, output, "First occurence of a station should go through");
        assertNull(output.getLastChangeTimestamp());
    }

    @Test
    public void deduplicator_shouldNotLetThrough_whenStationUnchangedInLessThan15Minutes(){
        LocalDateTime now = DateTimeUtils.now();
        var newStation = station("a", 1, 2, true, DateTimeUtils.timestamp(now));
        var same = AvroStationUpdate.newBuilder(newStation).setLoadTimestamp(DateTimeUtils.timestamp(now.plusMinutes(10))).build();

        deduplicator.transform("a", newStation);
        var output = deduplicator.transform("a", same);

        assertNull(output, "If a station with 'same' characteristics come within 15 minutes, it should not be propagated");
    }

    @Test
    public void deduplicator_shouldLetThrough_whenStationUnchangedInMoreThan15Minutes(){
        LocalDateTime now = DateTimeUtils.now();
        var newStation = station("a", 1, 2, true, DateTimeUtils.timestamp(now));
        var same = AvroStationUpdate.newBuilder(newStation).setLoadTimestamp(DateTimeUtils.timestamp(now.plusMinutes(20))).build();

        deduplicator.transform("a", newStation);
        var output = deduplicator.transform("a", same);

        assertEquals(same, output, "If a station with 'same' characteristics come more than 15 minutes later, it should be propagated");
    }

    @Test
    public void deduplicator_shouldLetThrough_whenStationHasNotTheSameNumberOfBikes(){
        LocalDateTime now = DateTimeUtils.now();
        var newStation = station("a", 1, 2, true, DateTimeUtils.timestamp(now));
        var sameWithDifferentBikes = AvroStationUpdate.newBuilder(newStation)
                .setLoadTimestamp(DateTimeUtils.timestamp(now.plusMinutes(10)))
                .setElectricBikesAtStation(2)
                .setMechanicalBikesAtStation(3)
                .build();

        deduplicator.transform("a", newStation);
        var output = deduplicator.transform("a", sameWithDifferentBikes);

        assertEquals(sameWithDifferentBikes, output, "If a station has not the same number of bikes, it should be propagated");
        assertNull(output.getLastChangeTimestamp());
    }

    @Test
    public void deduplicator_shouldLetThrough_whenStationHasNotTheSameStatus(){
        LocalDateTime now = DateTimeUtils.now();
        var newStation = station("a", 1, 2, true, DateTimeUtils.timestamp(now));
        var sameWithDifferentStatus = AvroStationUpdate.newBuilder(newStation)
                .setLoadTimestamp(DateTimeUtils.timestamp(now.plusMinutes(10)))
                .setIsReturning(false)
                .build();

        deduplicator.transform("a", newStation);
        var output = deduplicator.transform("a", sameWithDifferentStatus);

        assertEquals(sameWithDifferentStatus, output, "If a station has not the same status, it should be propagated");
        assertNull(output.getLastChangeTimestamp());
    }

    @Test
    public void deduplicator_shouldUpdateDiffs_whenLettingThrough(){
        LocalDateTime now = DateTimeUtils.now();
        var newStation = station("a", 1, 2, true, DateTimeUtils.timestamp(now));
        var sameWithDifferentBikes = AvroStationUpdate.newBuilder(newStation)
                .setLoadTimestamp(DateTimeUtils.timestamp(now.plusMinutes(10)))
                .setElectricBikesAtStation(3)
                .setMechanicalBikesAtStation(1)
                .build();

        deduplicator.transform("a", newStation);
        var output = deduplicator.transform("a", sameWithDifferentBikes);

        assertEquals(2, output.getElectricBikesReturned());
        assertEquals(0, output.getElectricBikesRented());
        assertEquals(0, output.getMechanicalBikesReturned());
        assertEquals(1, output.getMechanicalBikesRented());
    }

    @Test
    public void deduplicator_shouldUpdateLastMovement_whenStationUnchangedForMoreThan15Minutes(){
        LocalDateTime now = DateTimeUtils.now();
        var newStation = station("a", 1, 2, true, DateTimeUtils.timestamp(now));
        var same = AvroStationUpdate.newBuilder(newStation).setLoadTimestamp(DateTimeUtils.timestamp(now.plusMinutes(20))).build();

        deduplicator.transform("a", newStation);
        var firstStaleOutput = deduplicator.transform("a", same);

        assertEquals(newStation.getLoadTimestamp(), firstStaleOutput.getLastChangeTimestamp(), "When it's the first stale occurrence, the last movement should represent the loadtimestamp of the previous one");

        var sameLater = AvroStationUpdate.newBuilder(newStation).setLoadTimestamp(DateTimeUtils.timestamp(now.plusMinutes(40))).build();
        var secondStaleOutput = deduplicator.transform("a", sameLater);

        assertEquals(newStation.getLoadTimestamp(), secondStaleOutput.getLastChangeTimestamp(), "Subsequent occurrences should still have the first occurrence loadTimestamp as their lastChangeTimestamp");

        var sameWithDifferentBikes = AvroStationUpdate.newBuilder(newStation).setLoadTimestamp(DateTimeUtils.timestamp(now.plusMinutes(45))).setMechanicalBikesAtStation(5).build();
        var differentOutput = deduplicator.transform("a", sameWithDifferentBikes);

        assertNull(differentOutput.getLastChangeTimestamp(), "When a 'changed' station comes, lastChangeTimestamp should be reset to null");
    }

    private AvroStationUpdate station(String stationCode, int electric, int mechanical, boolean isWorking, long loadTimestamp){
        return AvroStationUpdate.newBuilder()
                .setStationCode(stationCode)
                .setStationName("station "+stationCode)
                .setStationCapacity(20)
                .setCoordinates(AvroCoordinates.newBuilder().setLongitude(1.0).setLatitude(1.0).build())
                .setElectricBikesAtStation(electric)
                .setElectricBikesRented(0).setElectricBikesReturned(0)
                .setMechanicalBikesAtStation(mechanical)
                .setMechanicalBikesRented(0).setMechanicalBikesReturned(0)
                .setIsInstalled(isWorking)
                .setIsRenting(isWorking)
                .setIsReturning(isWorking)
                .setLoadTimestamp(loadTimestamp)
                .setAvailabilityTimestamp(loadTimestamp-60*1000)
                .build();
    }
}