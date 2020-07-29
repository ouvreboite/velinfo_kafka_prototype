package velibstreaming.kafka.producer.mapper;

import org.apache.avro.specific.SpecificRecord;

@FunctionalInterface
public interface TimestampMapper<A extends SpecificRecord> {
    long extractTimestamp(A avroRecord);
}
