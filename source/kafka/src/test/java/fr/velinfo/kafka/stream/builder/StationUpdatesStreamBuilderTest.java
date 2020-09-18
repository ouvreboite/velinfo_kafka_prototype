package fr.velinfo.kafka.stream.builder;

import fr.velinfo.kafka.stream.StreamUtils;
import fr.velinfo.kafka.utils.DateTimeUtils;
import fr.velinfo.properties.ConnectionConfiguration;
import io.confluent.kafka.schemaregistry.testutil.MockSchemaRegistry;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.TestInputTopic;
import org.apache.kafka.streams.TestOutputTopic;
import org.apache.kafka.streams.TopologyTestDriver;
import org.apache.kafka.streams.test.TestRecord;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import fr.velinfo.avro.record.source.AvroCoordinates;
import fr.velinfo.avro.record.source.AvroStationAvailability;
import fr.velinfo.avro.record.stream.AvroStationUpdate;

import java.time.Duration;
import java.time.ZoneOffset;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StationUpdatesStreamBuilderTest {

    private TopologyTestDriver testDriver;
    private TestInputTopic<String, AvroStationAvailability> inputTopic;
    private TestOutputTopic<String, AvroStationUpdate> outputTopic;

    @BeforeEach
    public void setup() {
        ConnectionConfiguration.getInstance().setMockSchemaRegistryUrl(StreamTestUtils.getSchemaRegistryUrl());
        StreamsBuilder builder = new StreamsBuilder();

        var inputStream = builder.<String, AvroStationAvailability>stream("input-topic");
        var resultStream = new StationUpdatesStreamBuilder().build(builder, inputStream);
        resultStream.to("result-topic");

        this.testDriver = new TopologyTestDriver(builder.build(), StreamTestUtils.getStreamConfig());

        this.inputTopic = testDriver.createInputTopic(
                "input-topic",
                new StringSerializer(),
                StreamUtils.<AvroStationAvailability>avroSerde().serializer());

        this.outputTopic = testDriver.createOutputTopic(
                "result-topic",
                new StringDeserializer(),
                StreamUtils.<AvroStationUpdate>avroSerde().deserializer());
    }

    @AfterEach
    public void tearDown() {
        testDriver.close();
        MockSchemaRegistry.dropScope(StreamTestUtils.getSchemaRegistryUrl());
        ConnectionConfiguration.getInstance().setMockSchemaRegistryUrl(null);
    }

    @Test
    public void stationUpdate_shouldNotBeEmitted_whenAvailabilityIsADuplicate() {
        inputTopic.pipeInput("a", availability("a",0,0));
        assertEquals(1, outputTopic.readRecordsToList().size(), "When a station availability is emitted first, it should be translated to an (initial) update");

        inputTopic.pipeInput("a", availability("a",0,0));
        assertEquals(0, outputTopic.readRecordsToList().size(), "When the same availability is re-emitted, no new update should be emitted");

        inputTopic.pipeInput("a", availability("a",1,0));
        assertEquals(1, outputTopic.readRecordsToList().size(), "When a different availability is emitted, a new update should be emitted");

        inputTopic.pipeInput("b", availability("b",1,0));
        assertEquals(1, outputTopic.readRecordsToList().size(), "When a station availability is emitted first, it should be translated to an (initial) update");
    }

    @Test
    public void stationUpdate_shouldBeEmittedWithAStaleTimestamp_whenAvailabilityIsADuplicateForMoreThan15Minutes() {
        AvroStationAvailability firstEmitted = availability("a", 0, 0);
        inputTopic.pipeInput("a", firstEmitted);
        assertEquals(1, outputTopic.readRecordsToList().size(), "When a station availability is emitted first, it should be translated to an (initial) update");

        inputTopic.pipeInput("a", availability("a",0,0));
        assertEquals(0, outputTopic.readRecordsToList().size(), "When the same availability is re-emitted, no new update should be emitted");

        AvroStationAvailability newerAvailability = availability("a", 0, 0);
        newerAvailability.setLoadTimestamp(newerAvailability.getLoadTimestamp()+Duration.ofMinutes(16).toMillis());
        inputTopic.pipeInput("a", newerAvailability);

        List<TestRecord<String, AvroStationUpdate>> records = outputTopic.readRecordsToList();
        assertEquals(1, records.size(), "When the same availability is re-emitted more than 15 minutes later, a new update should be emitted");
        assertEquals(firstEmitted.getLoadTimestamp(),records.get(0).getValue().getLastChangeTimestamp(), "When the same availability is re-emitted more than 15 minutes later, a new update should be emitted and its staleSinceTimestamp should use the loadTimestamp of the first occurrence");

        inputTopic.pipeInput("a", newerAvailability);
        assertEquals(0, outputTopic.readRecordsToList().size(), "When the same availability is re-emitted, no new update should be emitted");

        newerAvailability.setLoadTimestamp(newerAvailability.getLoadTimestamp()+Duration.ofMinutes(16).toMillis());
        inputTopic.pipeInput("a", newerAvailability);
        records = outputTopic.readRecordsToList();
        assertEquals(1, records.size(), "When the same availability is re-emitted more than 15 minutes later, a new update should be emitted");
        assertEquals(firstEmitted.getLoadTimestamp(),records.get(0).getValue().getLastChangeTimestamp(), "When the same availability is re-emitted more than 15 minutes later, a new update should be emitted and its staleSinceTimestamp should use the loadTimestamp of the first occurrence");

    }

    private AvroStationAvailability availability(String stationCode, int electricBikes, int mechanicalBikes) {
        return AvroStationAvailability.newBuilder()
                .setStationCode(stationCode)
                .setElectricBikesAtStation(electricBikes)
                .setMechanicalBikesAtStation(mechanicalBikes)
                .setStationName("station"+stationCode)
                .setStationCapacity(32)
                .setIsRenting(true)
                .setIsReturning(true)
                .setIsInstalled(true)
                .setCoordinates(AvroCoordinates.newBuilder().setLatitude(1.0).setLongitude(1.0).build())
                .setLoadTimestamp(DateTimeUtils.now().toEpochSecond(ZoneOffset.UTC))
                .setAvailabilityTimestamp(DateTimeUtils.now().toEpochSecond(ZoneOffset.UTC))
                .build();
    }
}