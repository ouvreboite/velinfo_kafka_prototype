/**
 * Autogenerated by Avro
 *
 * DO NOT EDIT DIRECTLY
 */
package velibstreaming.avro.record.stream;
@org.apache.avro.specific.AvroGenerated
public enum StationStatus implements org.apache.avro.generic.GenericEnumSymbol<StationStatus> {
  OK, NOT_INSTALLED, NOT_RENTING, NOT_RETURNING, NOT_RENTING_NOR_RETURNING, STALE  ;
  public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"enum\",\"name\":\"StationStatus\",\"namespace\":\"velibstreaming.avro.record.stream\",\"symbols\":[\"OK\",\"NOT_INSTALLED\",\"NOT_RENTING\",\"NOT_RETURNING\",\"NOT_RENTING_NOR_RETURNING\",\"STALE\"]}");
  public static org.apache.avro.Schema getClassSchema() { return SCHEMA$; }
  public org.apache.avro.Schema getSchema() { return SCHEMA$; }
}
