package fr.velinfo.kafka.stream.builder;

import io.confluent.kafka.schemaregistry.testutil.MockSchemaRegistry;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.TestInputTopic;
import org.apache.kafka.streams.TestOutputTopic;
import org.apache.kafka.streams.TopologyTestDriver;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import fr.velinfo.avro.record.source.AvroBicycleCount;
import fr.velinfo.avro.record.source.AvroCoordinates;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CountUpdatesStreamBuilderTest {

    private TopologyTestDriver testDriver;
    private TestInputTopic<String, AvroBicycleCount> inputTopic;
    private TestOutputTopic<String, AvroBicycleCount> outputTopic;

    @BeforeEach
    public void setup() {
        StreamsBuilder builder = new StreamsBuilder();

        var inputStream = builder.<String, AvroBicycleCount>stream("input-topic");
        var resultStream = new CountUpdatesStreamBuilder().build(builder, inputStream);
        resultStream.to("result-topic");

        this.testDriver = new TopologyTestDriver(builder.build(), StreamTestUtils.getStreamConfig());

        this.inputTopic = testDriver.createInputTopic(
                "input-topic",
                new StringSerializer(),
                StreamTestUtils.<AvroBicycleCount>avroSerde().serializer());

        this.outputTopic = testDriver.createOutputTopic(
                "result-topic",
                new StringDeserializer(),
                StreamTestUtils.<AvroBicycleCount>avroSerde().deserializer());
    }

    @Test
    public void bicycleCount_shouldNotBeEmitted_whenCountIsADuplicate() throws InterruptedException {
        AvroBicycleCount firstCount = count("a", 0);
        inputTopic.pipeInput("a", firstCount);
        assertEquals(1, outputTopic.readRecordsToList().size(), "When a bicycle count is emitted first, it should go through");

        inputTopic.pipeInput("a", firstCount);
        assertEquals(0, outputTopic.readRecordsToList().size(), "When the same count is re-emitted, it should not go through");

        AvroBicycleCount newCount = count("a", 0);
        inputTopic.pipeInput("a", newCount);
        assertEquals(1, outputTopic.readRecordsToList().size(), "When a different count is emitted, it should go through");

        inputTopic.pipeInput("a", newCount);
        assertEquals(0, outputTopic.readRecordsToList().size(), "When the same count is re-emitted, it should not go through");
    }


    private AvroBicycleCount count(String id, int count) {
        return AvroBicycleCount.newBuilder()
                .setCounterId(id)
                .setCount(count)
                .setCoordinates(AvroCoordinates.newBuilder().setLatitude(1.0).setLongitude(1.0).build())
                .setCountTimestamp(new Date().getTime())
                .build();
    }

    @AfterEach
    public void tearDown() {
        testDriver.close();
        MockSchemaRegistry.dropScope(StreamTestUtils.getSchemaRegistryUrl());
    }
}