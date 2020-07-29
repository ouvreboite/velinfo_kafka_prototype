/**
 * Autogenerated by Avro
 *
 * DO NOT EDIT DIRECTLY
 */
package velibstreaming.avro.record.stream;

import org.apache.avro.generic.GenericArray;
import org.apache.avro.specific.SpecificData;
import org.apache.avro.util.Utf8;
import org.apache.avro.message.BinaryMessageEncoder;
import org.apache.avro.message.BinaryMessageDecoder;
import org.apache.avro.message.SchemaStore;

@org.apache.avro.specific.AvroGenerated
public class AvroStationStatus extends org.apache.avro.specific.SpecificRecordBase implements org.apache.avro.specific.SpecificRecord {
  private static final long serialVersionUID = -8526471743722495147L;
  public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"record\",\"name\":\"AvroStationStatus\",\"namespace\":\"velibstreaming.avro.record.stream\",\"fields\":[{\"name\":\"stationCode\",\"type\":{\"type\":\"string\",\"avro.java.string\":\"String\"}},{\"name\":\"status\",\"type\":{\"type\":\"enum\",\"name\":\"StationStatus\",\"symbols\":[\"OK\",\"NOT_INSTALLED\",\"NOT_RENTING\",\"NOT_RETURNING\",\"NOT_RENTING_NOR_RETURNING\",\"STALE\"]},\"default\":\"OK\"},{\"name\":\"staleSince\",\"type\":[\"null\",\"long\"],\"default\":null,\"logicalType\":\"timestamp-millis\"}]}");
  public static org.apache.avro.Schema getClassSchema() { return SCHEMA$; }

  private static SpecificData MODEL$ = new SpecificData();

  private static final BinaryMessageEncoder<AvroStationStatus> ENCODER =
      new BinaryMessageEncoder<AvroStationStatus>(MODEL$, SCHEMA$);

  private static final BinaryMessageDecoder<AvroStationStatus> DECODER =
      new BinaryMessageDecoder<AvroStationStatus>(MODEL$, SCHEMA$);

  /**
   * Return the BinaryMessageEncoder instance used by this class.
   * @return the message encoder used by this class
   */
  public static BinaryMessageEncoder<AvroStationStatus> getEncoder() {
    return ENCODER;
  }

  /**
   * Return the BinaryMessageDecoder instance used by this class.
   * @return the message decoder used by this class
   */
  public static BinaryMessageDecoder<AvroStationStatus> getDecoder() {
    return DECODER;
  }

  /**
   * Create a new BinaryMessageDecoder instance for this class that uses the specified {@link SchemaStore}.
   * @param resolver a {@link SchemaStore} used to find schemas by fingerprint
   * @return a BinaryMessageDecoder instance for this class backed by the given SchemaStore
   */
  public static BinaryMessageDecoder<AvroStationStatus> createDecoder(SchemaStore resolver) {
    return new BinaryMessageDecoder<AvroStationStatus>(MODEL$, SCHEMA$, resolver);
  }

  /**
   * Serializes this AvroStationStatus to a ByteBuffer.
   * @return a buffer holding the serialized data for this instance
   * @throws java.io.IOException if this instance could not be serialized
   */
  public java.nio.ByteBuffer toByteBuffer() throws java.io.IOException {
    return ENCODER.encode(this);
  }

  /**
   * Deserializes a AvroStationStatus from a ByteBuffer.
   * @param b a byte buffer holding serialized data for an instance of this class
   * @return a AvroStationStatus instance decoded from the given buffer
   * @throws java.io.IOException if the given bytes could not be deserialized into an instance of this class
   */
  public static AvroStationStatus fromByteBuffer(
      java.nio.ByteBuffer b) throws java.io.IOException {
    return DECODER.decode(b);
  }

  @Deprecated public java.lang.String stationCode;
  @Deprecated public velibstreaming.avro.record.stream.StationStatus status;
  @Deprecated public java.lang.Long staleSince;

  /**
   * Default constructor.  Note that this does not initialize fields
   * to their default values from the schema.  If that is desired then
   * one should use <code>newBuilder()</code>.
   */
  public AvroStationStatus() {}

  /**
   * All-args constructor.
   * @param stationCode The new value for stationCode
   * @param status The new value for status
   * @param staleSince The new value for staleSince
   */
  public AvroStationStatus(java.lang.String stationCode, velibstreaming.avro.record.stream.StationStatus status, java.lang.Long staleSince) {
    this.stationCode = stationCode;
    this.status = status;
    this.staleSince = staleSince;
  }

  public org.apache.avro.specific.SpecificData getSpecificData() { return MODEL$; }
  public org.apache.avro.Schema getSchema() { return SCHEMA$; }
  // Used by DatumWriter.  Applications should not call.
  public java.lang.Object get(int field$) {
    switch (field$) {
    case 0: return stationCode;
    case 1: return status;
    case 2: return staleSince;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }

  // Used by DatumReader.  Applications should not call.
  @SuppressWarnings(value="unchecked")
  public void put(int field$, java.lang.Object value$) {
    switch (field$) {
    case 0: stationCode = value$ != null ? value$.toString() : null; break;
    case 1: status = (velibstreaming.avro.record.stream.StationStatus)value$; break;
    case 2: staleSince = (java.lang.Long)value$; break;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }

  /**
   * Gets the value of the 'stationCode' field.
   * @return The value of the 'stationCode' field.
   */
  public java.lang.String getStationCode() {
    return stationCode;
  }


  /**
   * Sets the value of the 'stationCode' field.
   * @param value the value to set.
   */
  public void setStationCode(java.lang.String value) {
    this.stationCode = value;
  }

  /**
   * Gets the value of the 'status' field.
   * @return The value of the 'status' field.
   */
  public velibstreaming.avro.record.stream.StationStatus getStatus() {
    return status;
  }


  /**
   * Sets the value of the 'status' field.
   * @param value the value to set.
   */
  public void setStatus(velibstreaming.avro.record.stream.StationStatus value) {
    this.status = value;
  }

  /**
   * Gets the value of the 'staleSince' field.
   * @return The value of the 'staleSince' field.
   */
  public java.lang.Long getStaleSince() {
    return staleSince;
  }


  /**
   * Sets the value of the 'staleSince' field.
   * @param value the value to set.
   */
  public void setStaleSince(java.lang.Long value) {
    this.staleSince = value;
  }

  /**
   * Creates a new AvroStationStatus RecordBuilder.
   * @return A new AvroStationStatus RecordBuilder
   */
  public static velibstreaming.avro.record.stream.AvroStationStatus.Builder newBuilder() {
    return new velibstreaming.avro.record.stream.AvroStationStatus.Builder();
  }

  /**
   * Creates a new AvroStationStatus RecordBuilder by copying an existing Builder.
   * @param other The existing builder to copy.
   * @return A new AvroStationStatus RecordBuilder
   */
  public static velibstreaming.avro.record.stream.AvroStationStatus.Builder newBuilder(velibstreaming.avro.record.stream.AvroStationStatus.Builder other) {
    if (other == null) {
      return new velibstreaming.avro.record.stream.AvroStationStatus.Builder();
    } else {
      return new velibstreaming.avro.record.stream.AvroStationStatus.Builder(other);
    }
  }

  /**
   * Creates a new AvroStationStatus RecordBuilder by copying an existing AvroStationStatus instance.
   * @param other The existing instance to copy.
   * @return A new AvroStationStatus RecordBuilder
   */
  public static velibstreaming.avro.record.stream.AvroStationStatus.Builder newBuilder(velibstreaming.avro.record.stream.AvroStationStatus other) {
    if (other == null) {
      return new velibstreaming.avro.record.stream.AvroStationStatus.Builder();
    } else {
      return new velibstreaming.avro.record.stream.AvroStationStatus.Builder(other);
    }
  }

  /**
   * RecordBuilder for AvroStationStatus instances.
   */
  @org.apache.avro.specific.AvroGenerated
  public static class Builder extends org.apache.avro.specific.SpecificRecordBuilderBase<AvroStationStatus>
    implements org.apache.avro.data.RecordBuilder<AvroStationStatus> {

    private java.lang.String stationCode;
    private velibstreaming.avro.record.stream.StationStatus status;
    private java.lang.Long staleSince;

    /** Creates a new Builder */
    private Builder() {
      super(SCHEMA$);
    }

    /**
     * Creates a Builder by copying an existing Builder.
     * @param other The existing Builder to copy.
     */
    private Builder(velibstreaming.avro.record.stream.AvroStationStatus.Builder other) {
      super(other);
      if (isValidValue(fields()[0], other.stationCode)) {
        this.stationCode = data().deepCopy(fields()[0].schema(), other.stationCode);
        fieldSetFlags()[0] = other.fieldSetFlags()[0];
      }
      if (isValidValue(fields()[1], other.status)) {
        this.status = data().deepCopy(fields()[1].schema(), other.status);
        fieldSetFlags()[1] = other.fieldSetFlags()[1];
      }
      if (isValidValue(fields()[2], other.staleSince)) {
        this.staleSince = data().deepCopy(fields()[2].schema(), other.staleSince);
        fieldSetFlags()[2] = other.fieldSetFlags()[2];
      }
    }

    /**
     * Creates a Builder by copying an existing AvroStationStatus instance
     * @param other The existing instance to copy.
     */
    private Builder(velibstreaming.avro.record.stream.AvroStationStatus other) {
      super(SCHEMA$);
      if (isValidValue(fields()[0], other.stationCode)) {
        this.stationCode = data().deepCopy(fields()[0].schema(), other.stationCode);
        fieldSetFlags()[0] = true;
      }
      if (isValidValue(fields()[1], other.status)) {
        this.status = data().deepCopy(fields()[1].schema(), other.status);
        fieldSetFlags()[1] = true;
      }
      if (isValidValue(fields()[2], other.staleSince)) {
        this.staleSince = data().deepCopy(fields()[2].schema(), other.staleSince);
        fieldSetFlags()[2] = true;
      }
    }

    /**
      * Gets the value of the 'stationCode' field.
      * @return The value.
      */
    public java.lang.String getStationCode() {
      return stationCode;
    }


    /**
      * Sets the value of the 'stationCode' field.
      * @param value The value of 'stationCode'.
      * @return This builder.
      */
    public velibstreaming.avro.record.stream.AvroStationStatus.Builder setStationCode(java.lang.String value) {
      validate(fields()[0], value);
      this.stationCode = value;
      fieldSetFlags()[0] = true;
      return this;
    }

    /**
      * Checks whether the 'stationCode' field has been set.
      * @return True if the 'stationCode' field has been set, false otherwise.
      */
    public boolean hasStationCode() {
      return fieldSetFlags()[0];
    }


    /**
      * Clears the value of the 'stationCode' field.
      * @return This builder.
      */
    public velibstreaming.avro.record.stream.AvroStationStatus.Builder clearStationCode() {
      stationCode = null;
      fieldSetFlags()[0] = false;
      return this;
    }

    /**
      * Gets the value of the 'status' field.
      * @return The value.
      */
    public velibstreaming.avro.record.stream.StationStatus getStatus() {
      return status;
    }


    /**
      * Sets the value of the 'status' field.
      * @param value The value of 'status'.
      * @return This builder.
      */
    public velibstreaming.avro.record.stream.AvroStationStatus.Builder setStatus(velibstreaming.avro.record.stream.StationStatus value) {
      validate(fields()[1], value);
      this.status = value;
      fieldSetFlags()[1] = true;
      return this;
    }

    /**
      * Checks whether the 'status' field has been set.
      * @return True if the 'status' field has been set, false otherwise.
      */
    public boolean hasStatus() {
      return fieldSetFlags()[1];
    }


    /**
      * Clears the value of the 'status' field.
      * @return This builder.
      */
    public velibstreaming.avro.record.stream.AvroStationStatus.Builder clearStatus() {
      status = null;
      fieldSetFlags()[1] = false;
      return this;
    }

    /**
      * Gets the value of the 'staleSince' field.
      * @return The value.
      */
    public java.lang.Long getStaleSince() {
      return staleSince;
    }


    /**
      * Sets the value of the 'staleSince' field.
      * @param value The value of 'staleSince'.
      * @return This builder.
      */
    public velibstreaming.avro.record.stream.AvroStationStatus.Builder setStaleSince(java.lang.Long value) {
      validate(fields()[2], value);
      this.staleSince = value;
      fieldSetFlags()[2] = true;
      return this;
    }

    /**
      * Checks whether the 'staleSince' field has been set.
      * @return True if the 'staleSince' field has been set, false otherwise.
      */
    public boolean hasStaleSince() {
      return fieldSetFlags()[2];
    }


    /**
      * Clears the value of the 'staleSince' field.
      * @return This builder.
      */
    public velibstreaming.avro.record.stream.AvroStationStatus.Builder clearStaleSince() {
      staleSince = null;
      fieldSetFlags()[2] = false;
      return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public AvroStationStatus build() {
      try {
        AvroStationStatus record = new AvroStationStatus();
        record.stationCode = fieldSetFlags()[0] ? this.stationCode : (java.lang.String) defaultValue(fields()[0]);
        record.status = fieldSetFlags()[1] ? this.status : (velibstreaming.avro.record.stream.StationStatus) defaultValue(fields()[1]);
        record.staleSince = fieldSetFlags()[2] ? this.staleSince : (java.lang.Long) defaultValue(fields()[2]);
        return record;
      } catch (org.apache.avro.AvroMissingFieldException e) {
        throw e;
      } catch (java.lang.Exception e) {
        throw new org.apache.avro.AvroRuntimeException(e);
      }
    }
  }

  @SuppressWarnings("unchecked")
  private static final org.apache.avro.io.DatumWriter<AvroStationStatus>
    WRITER$ = (org.apache.avro.io.DatumWriter<AvroStationStatus>)MODEL$.createDatumWriter(SCHEMA$);

  @Override public void writeExternal(java.io.ObjectOutput out)
    throws java.io.IOException {
    WRITER$.write(this, SpecificData.getEncoder(out));
  }

  @SuppressWarnings("unchecked")
  private static final org.apache.avro.io.DatumReader<AvroStationStatus>
    READER$ = (org.apache.avro.io.DatumReader<AvroStationStatus>)MODEL$.createDatumReader(SCHEMA$);

  @Override public void readExternal(java.io.ObjectInput in)
    throws java.io.IOException {
    READER$.read(this, SpecificData.getDecoder(in));
  }

  @Override protected boolean hasCustomCoders() { return true; }

  @Override public void customEncode(org.apache.avro.io.Encoder out)
    throws java.io.IOException
  {
    out.writeString(this.stationCode);

    out.writeEnum(this.status.ordinal());

    if (this.staleSince == null) {
      out.writeIndex(0);
      out.writeNull();
    } else {
      out.writeIndex(1);
      out.writeLong(this.staleSince);
    }

  }

  @Override public void customDecode(org.apache.avro.io.ResolvingDecoder in)
    throws java.io.IOException
  {
    org.apache.avro.Schema.Field[] fieldOrder = in.readFieldOrderIfDiff();
    if (fieldOrder == null) {
      this.stationCode = in.readString();

      this.status = velibstreaming.avro.record.stream.StationStatus.values()[in.readEnum()];

      if (in.readIndex() != 1) {
        in.readNull();
        this.staleSince = null;
      } else {
        this.staleSince = in.readLong();
      }

    } else {
      for (int i = 0; i < 3; i++) {
        switch (fieldOrder[i].pos()) {
        case 0:
          this.stationCode = in.readString();
          break;

        case 1:
          this.status = velibstreaming.avro.record.stream.StationStatus.values()[in.readEnum()];
          break;

        case 2:
          if (in.readIndex() != 1) {
            in.readNull();
            this.staleSince = null;
          } else {
            this.staleSince = in.readLong();
          }
          break;

        default:
          throw new java.io.IOException("Corrupt ResolvingDecoder.");
        }
      }
    }
  }
}









