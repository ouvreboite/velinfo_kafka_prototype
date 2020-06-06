/**
 * Autogenerated by Avro
 *
 * DO NOT EDIT DIRECTLY
 */
package velibstreaming.avro.record;

import org.apache.avro.generic.GenericArray;
import org.apache.avro.specific.SpecificData;
import org.apache.avro.util.Utf8;
import org.apache.avro.message.BinaryMessageEncoder;
import org.apache.avro.message.BinaryMessageDecoder;
import org.apache.avro.message.SchemaStore;

@org.apache.avro.specific.AvroGenerated
public class AvroBicycleCount extends org.apache.avro.specific.SpecificRecordBase implements org.apache.avro.specific.SpecificRecord {
  private static final long serialVersionUID = 2891856589158531201L;
  public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"record\",\"name\":\"AvroBicycleCount\",\"namespace\":\"velibstreaming.avro.record\",\"fields\":[{\"name\":\"counterId\",\"type\":\"string\"},{\"name\":\"count\",\"type\":\"int\"},{\"name\":\"countTimestamp\",\"type\":\"long\",\"logicalType\":\"timestamp-millis\"}]}");
  public static org.apache.avro.Schema getClassSchema() { return SCHEMA$; }

  private static SpecificData MODEL$ = new SpecificData();

  private static final BinaryMessageEncoder<AvroBicycleCount> ENCODER =
      new BinaryMessageEncoder<AvroBicycleCount>(MODEL$, SCHEMA$);

  private static final BinaryMessageDecoder<AvroBicycleCount> DECODER =
      new BinaryMessageDecoder<AvroBicycleCount>(MODEL$, SCHEMA$);

  /**
   * Return the BinaryMessageEncoder instance used by this class.
   * @return the message encoder used by this class
   */
  public static BinaryMessageEncoder<AvroBicycleCount> getEncoder() {
    return ENCODER;
  }

  /**
   * Return the BinaryMessageDecoder instance used by this class.
   * @return the message decoder used by this class
   */
  public static BinaryMessageDecoder<AvroBicycleCount> getDecoder() {
    return DECODER;
  }

  /**
   * Create a new BinaryMessageDecoder instance for this class that uses the specified {@link SchemaStore}.
   * @param resolver a {@link SchemaStore} used to find schemas by fingerprint
   * @return a BinaryMessageDecoder instance for this class backed by the given SchemaStore
   */
  public static BinaryMessageDecoder<AvroBicycleCount> createDecoder(SchemaStore resolver) {
    return new BinaryMessageDecoder<AvroBicycleCount>(MODEL$, SCHEMA$, resolver);
  }

  /**
   * Serializes this AvroBicycleCount to a ByteBuffer.
   * @return a buffer holding the serialized data for this instance
   * @throws java.io.IOException if this instance could not be serialized
   */
  public java.nio.ByteBuffer toByteBuffer() throws java.io.IOException {
    return ENCODER.encode(this);
  }

  /**
   * Deserializes a AvroBicycleCount from a ByteBuffer.
   * @param b a byte buffer holding serialized data for an instance of this class
   * @return a AvroBicycleCount instance decoded from the given buffer
   * @throws java.io.IOException if the given bytes could not be deserialized into an instance of this class
   */
  public static AvroBicycleCount fromByteBuffer(
      java.nio.ByteBuffer b) throws java.io.IOException {
    return DECODER.decode(b);
  }

  @Deprecated public java.lang.CharSequence counterId;
  @Deprecated public int count;
  @Deprecated public long countTimestamp;

  /**
   * Default constructor.  Note that this does not initialize fields
   * to their default values from the schema.  If that is desired then
   * one should use <code>newBuilder()</code>.
   */
  public AvroBicycleCount() {}

  /**
   * All-args constructor.
   * @param counterId The new value for counterId
   * @param count The new value for count
   * @param countTimestamp The new value for countTimestamp
   */
  public AvroBicycleCount(java.lang.CharSequence counterId, java.lang.Integer count, java.lang.Long countTimestamp) {
    this.counterId = counterId;
    this.count = count;
    this.countTimestamp = countTimestamp;
  }

  public org.apache.avro.specific.SpecificData getSpecificData() { return MODEL$; }
  public org.apache.avro.Schema getSchema() { return SCHEMA$; }
  // Used by DatumWriter.  Applications should not call.
  public java.lang.Object get(int field$) {
    switch (field$) {
    case 0: return counterId;
    case 1: return count;
    case 2: return countTimestamp;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }

  // Used by DatumReader.  Applications should not call.
  @SuppressWarnings(value="unchecked")
  public void put(int field$, java.lang.Object value$) {
    switch (field$) {
    case 0: counterId = (java.lang.CharSequence)value$; break;
    case 1: count = (java.lang.Integer)value$; break;
    case 2: countTimestamp = (java.lang.Long)value$; break;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }

  /**
   * Gets the value of the 'counterId' field.
   * @return The value of the 'counterId' field.
   */
  public java.lang.CharSequence getCounterId() {
    return counterId;
  }


  /**
   * Sets the value of the 'counterId' field.
   * @param value the value to set.
   */
  public void setCounterId(java.lang.CharSequence value) {
    this.counterId = value;
  }

  /**
   * Gets the value of the 'count' field.
   * @return The value of the 'count' field.
   */
  public int getCount() {
    return count;
  }


  /**
   * Sets the value of the 'count' field.
   * @param value the value to set.
   */
  public void setCount(int value) {
    this.count = value;
  }

  /**
   * Gets the value of the 'countTimestamp' field.
   * @return The value of the 'countTimestamp' field.
   */
  public long getCountTimestamp() {
    return countTimestamp;
  }


  /**
   * Sets the value of the 'countTimestamp' field.
   * @param value the value to set.
   */
  public void setCountTimestamp(long value) {
    this.countTimestamp = value;
  }

  /**
   * Creates a new AvroBicycleCount RecordBuilder.
   * @return A new AvroBicycleCount RecordBuilder
   */
  public static velibstreaming.avro.record.AvroBicycleCount.Builder newBuilder() {
    return new velibstreaming.avro.record.AvroBicycleCount.Builder();
  }

  /**
   * Creates a new AvroBicycleCount RecordBuilder by copying an existing Builder.
   * @param other The existing builder to copy.
   * @return A new AvroBicycleCount RecordBuilder
   */
  public static velibstreaming.avro.record.AvroBicycleCount.Builder newBuilder(velibstreaming.avro.record.AvroBicycleCount.Builder other) {
    if (other == null) {
      return new velibstreaming.avro.record.AvroBicycleCount.Builder();
    } else {
      return new velibstreaming.avro.record.AvroBicycleCount.Builder(other);
    }
  }

  /**
   * Creates a new AvroBicycleCount RecordBuilder by copying an existing AvroBicycleCount instance.
   * @param other The existing instance to copy.
   * @return A new AvroBicycleCount RecordBuilder
   */
  public static velibstreaming.avro.record.AvroBicycleCount.Builder newBuilder(velibstreaming.avro.record.AvroBicycleCount other) {
    if (other == null) {
      return new velibstreaming.avro.record.AvroBicycleCount.Builder();
    } else {
      return new velibstreaming.avro.record.AvroBicycleCount.Builder(other);
    }
  }

  /**
   * RecordBuilder for AvroBicycleCount instances.
   */
  @org.apache.avro.specific.AvroGenerated
  public static class Builder extends org.apache.avro.specific.SpecificRecordBuilderBase<AvroBicycleCount>
    implements org.apache.avro.data.RecordBuilder<AvroBicycleCount> {

    private java.lang.CharSequence counterId;
    private int count;
    private long countTimestamp;

    /** Creates a new Builder */
    private Builder() {
      super(SCHEMA$);
    }

    /**
     * Creates a Builder by copying an existing Builder.
     * @param other The existing Builder to copy.
     */
    private Builder(velibstreaming.avro.record.AvroBicycleCount.Builder other) {
      super(other);
      if (isValidValue(fields()[0], other.counterId)) {
        this.counterId = data().deepCopy(fields()[0].schema(), other.counterId);
        fieldSetFlags()[0] = other.fieldSetFlags()[0];
      }
      if (isValidValue(fields()[1], other.count)) {
        this.count = data().deepCopy(fields()[1].schema(), other.count);
        fieldSetFlags()[1] = other.fieldSetFlags()[1];
      }
      if (isValidValue(fields()[2], other.countTimestamp)) {
        this.countTimestamp = data().deepCopy(fields()[2].schema(), other.countTimestamp);
        fieldSetFlags()[2] = other.fieldSetFlags()[2];
      }
    }

    /**
     * Creates a Builder by copying an existing AvroBicycleCount instance
     * @param other The existing instance to copy.
     */
    private Builder(velibstreaming.avro.record.AvroBicycleCount other) {
      super(SCHEMA$);
      if (isValidValue(fields()[0], other.counterId)) {
        this.counterId = data().deepCopy(fields()[0].schema(), other.counterId);
        fieldSetFlags()[0] = true;
      }
      if (isValidValue(fields()[1], other.count)) {
        this.count = data().deepCopy(fields()[1].schema(), other.count);
        fieldSetFlags()[1] = true;
      }
      if (isValidValue(fields()[2], other.countTimestamp)) {
        this.countTimestamp = data().deepCopy(fields()[2].schema(), other.countTimestamp);
        fieldSetFlags()[2] = true;
      }
    }

    /**
      * Gets the value of the 'counterId' field.
      * @return The value.
      */
    public java.lang.CharSequence getCounterId() {
      return counterId;
    }


    /**
      * Sets the value of the 'counterId' field.
      * @param value The value of 'counterId'.
      * @return This builder.
      */
    public velibstreaming.avro.record.AvroBicycleCount.Builder setCounterId(java.lang.CharSequence value) {
      validate(fields()[0], value);
      this.counterId = value;
      fieldSetFlags()[0] = true;
      return this;
    }

    /**
      * Checks whether the 'counterId' field has been set.
      * @return True if the 'counterId' field has been set, false otherwise.
      */
    public boolean hasCounterId() {
      return fieldSetFlags()[0];
    }


    /**
      * Clears the value of the 'counterId' field.
      * @return This builder.
      */
    public velibstreaming.avro.record.AvroBicycleCount.Builder clearCounterId() {
      counterId = null;
      fieldSetFlags()[0] = false;
      return this;
    }

    /**
      * Gets the value of the 'count' field.
      * @return The value.
      */
    public int getCount() {
      return count;
    }


    /**
      * Sets the value of the 'count' field.
      * @param value The value of 'count'.
      * @return This builder.
      */
    public velibstreaming.avro.record.AvroBicycleCount.Builder setCount(int value) {
      validate(fields()[1], value);
      this.count = value;
      fieldSetFlags()[1] = true;
      return this;
    }

    /**
      * Checks whether the 'count' field has been set.
      * @return True if the 'count' field has been set, false otherwise.
      */
    public boolean hasCount() {
      return fieldSetFlags()[1];
    }


    /**
      * Clears the value of the 'count' field.
      * @return This builder.
      */
    public velibstreaming.avro.record.AvroBicycleCount.Builder clearCount() {
      fieldSetFlags()[1] = false;
      return this;
    }

    /**
      * Gets the value of the 'countTimestamp' field.
      * @return The value.
      */
    public long getCountTimestamp() {
      return countTimestamp;
    }


    /**
      * Sets the value of the 'countTimestamp' field.
      * @param value The value of 'countTimestamp'.
      * @return This builder.
      */
    public velibstreaming.avro.record.AvroBicycleCount.Builder setCountTimestamp(long value) {
      validate(fields()[2], value);
      this.countTimestamp = value;
      fieldSetFlags()[2] = true;
      return this;
    }

    /**
      * Checks whether the 'countTimestamp' field has been set.
      * @return True if the 'countTimestamp' field has been set, false otherwise.
      */
    public boolean hasCountTimestamp() {
      return fieldSetFlags()[2];
    }


    /**
      * Clears the value of the 'countTimestamp' field.
      * @return This builder.
      */
    public velibstreaming.avro.record.AvroBicycleCount.Builder clearCountTimestamp() {
      fieldSetFlags()[2] = false;
      return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public AvroBicycleCount build() {
      try {
        AvroBicycleCount record = new AvroBicycleCount();
        record.counterId = fieldSetFlags()[0] ? this.counterId : (java.lang.CharSequence) defaultValue(fields()[0]);
        record.count = fieldSetFlags()[1] ? this.count : (java.lang.Integer) defaultValue(fields()[1]);
        record.countTimestamp = fieldSetFlags()[2] ? this.countTimestamp : (java.lang.Long) defaultValue(fields()[2]);
        return record;
      } catch (org.apache.avro.AvroMissingFieldException e) {
        throw e;
      } catch (java.lang.Exception e) {
        throw new org.apache.avro.AvroRuntimeException(e);
      }
    }
  }

  @SuppressWarnings("unchecked")
  private static final org.apache.avro.io.DatumWriter<AvroBicycleCount>
    WRITER$ = (org.apache.avro.io.DatumWriter<AvroBicycleCount>)MODEL$.createDatumWriter(SCHEMA$);

  @Override public void writeExternal(java.io.ObjectOutput out)
    throws java.io.IOException {
    WRITER$.write(this, SpecificData.getEncoder(out));
  }

  @SuppressWarnings("unchecked")
  private static final org.apache.avro.io.DatumReader<AvroBicycleCount>
    READER$ = (org.apache.avro.io.DatumReader<AvroBicycleCount>)MODEL$.createDatumReader(SCHEMA$);

  @Override public void readExternal(java.io.ObjectInput in)
    throws java.io.IOException {
    READER$.read(this, SpecificData.getDecoder(in));
  }

  @Override protected boolean hasCustomCoders() { return true; }

  @Override public void customEncode(org.apache.avro.io.Encoder out)
    throws java.io.IOException
  {
    out.writeString(this.counterId);

    out.writeInt(this.count);

    out.writeLong(this.countTimestamp);

  }

  @Override public void customDecode(org.apache.avro.io.ResolvingDecoder in)
    throws java.io.IOException
  {
    org.apache.avro.Schema.Field[] fieldOrder = in.readFieldOrderIfDiff();
    if (fieldOrder == null) {
      this.counterId = in.readString(this.counterId instanceof Utf8 ? (Utf8)this.counterId : null);

      this.count = in.readInt();

      this.countTimestamp = in.readLong();

    } else {
      for (int i = 0; i < 3; i++) {
        switch (fieldOrder[i].pos()) {
        case 0:
          this.counterId = in.readString(this.counterId instanceof Utf8 ? (Utf8)this.counterId : null);
          break;

        case 1:
          this.count = in.readInt();
          break;

        case 2:
          this.countTimestamp = in.readLong();
          break;

        default:
          throw new java.io.IOException("Corrupt ResolvingDecoder.");
        }
      }
    }
  }
}










