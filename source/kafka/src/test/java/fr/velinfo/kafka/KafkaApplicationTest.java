package fr.velinfo.kafka;

import fr.velinfo.kafka.producer.ProducerApplication;
import fr.velinfo.kafka.sink.SinkApplication;
import fr.velinfo.kafka.stream.StreamApplication;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static fr.velinfo.kafka.KafkaApplication.Application.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


@ExtendWith(MockitoExtension.class)
class KafkaApplicationTest {

    @Mock
    private ProducerApplication producerApplication;
    @Mock
    private StreamApplication streamApplication;
    @Mock
    private SinkApplication sinkApplication;
    @Mock
    private TopicCreator topicCreator;

    private KafkaApplication kafkaApplication;

    @BeforeEach
    void init(){
        this.kafkaApplication = new KafkaApplication(producerApplication,streamApplication,sinkApplication,topicCreator);
    }

    @Test
    void main_shouldThrowWhenApplicationUnknown() {
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> KafkaApplication.main(new String[]{"unknown","src/test/resources/connection.properties"})
        );
    }

    @Test
    void run_shouldStartProducer() {
        kafkaApplication.run(PRODUCER);
        verify(producerApplication, times(1)).start();
    }

    @Test
    void run_shouldStartStream() {
        kafkaApplication.run(STREAM);
        verify(streamApplication, times(1)).start();
    }

    @Test
    void run_shouldStartSink() {
        kafkaApplication.run(SINK);
        verify(sinkApplication, times(1)).start();
    }


}