package fr.velinfo.kafka.producer.mapper;

import org.apache.avro.specific.SpecificRecord;

@FunctionalInterface
public interface KeyMapper<A extends SpecificRecord> {
    String extractKey(A avroRecord);
}
