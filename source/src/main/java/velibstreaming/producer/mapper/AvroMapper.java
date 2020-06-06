package velibstreaming.producer.mapper;

import org.apache.avro.specific.SpecificRecord;

@FunctionalInterface
public interface AvroMapper<T,A extends SpecificRecord> {
    A map(T openDataRecord);
}
