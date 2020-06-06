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
public class AvroStationCharacteristics extends org.apache.avro.specific.SpecificRecordBase implements org.apache.avro.specific.SpecificRecord {
  private static final long serialVersionUID = -437174171127080330L;
  public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"record\",\"name\":\"AvroStationCharacteristics\",\"namespace\":\"velibstreaming.avro.record\",\"fields\":[{\"name\":\"stationCode\",\"type\":\"string\"},{\"name\":\"stationName\",\"type\":\"string\"},{\"name\":\"totalCapacity\",\"type\":\"int\"},{\"name\":\"latitude\",\"type\":\"double\"},{\"name\":\"longitude\",\"type\":\"double\"}]}");
  public static org.apache.avro.Schema getClassSchema() { return SCHEMA$; }

  private static SpecificData MODEL$ = new SpecificData();

  private static final BinaryMessageEncoder<AvroStationCharacteristics> ENCODER =
      new BinaryMessageEncoder<AvroStationCharacteristics>(MODEL$, SCHEMA$);

  private static final BinaryMessageDecoder<AvroStationCharacteristics> DECODER =
      new BinaryMessageDecoder<AvroStationCharacteristics>(MODEL$, SCHEMA$);

  /**
   * Return the BinaryMessageEncoder instance used by this class.
   * @return the message encoder used by this class
   */
  public static BinaryMessageEncoder<AvroStationCharacteristics> getEncoder() {
    return ENCODER;
  }

  /**
   * Return the BinaryMessageDecoder instance used by this class.
   * @return the message decoder used by this class
   */
  public static BinaryMessageDecoder<AvroStationCharacteristics> getDecoder() {
    return DECODER;
  }

  /**
   * Create a new BinaryMessageDecoder instance for this class that uses the specified {@link SchemaStore}.
   * @param resolver a {@link SchemaStore} used to find schemas by fingerprint
   * @return a BinaryMessageDecoder instance for this class backed by the given SchemaStore
   */
  public static BinaryMessageDecoder<AvroStationCharacteristics> createDecoder(SchemaStore resolver) {
    return new BinaryMessageDecoder<AvroStationCharacteristics>(MODEL$, SCHEMA$, resolver);
  }

  /**
   * Serializes this AvroStationCharacteristics to a ByteBuffer.
   * @return a buffer holding the serialized data for this instance
   * @throws java.io.IOException if this instance could not be serialized
   */
  public java.nio.ByteBuffer toByteBuffer() throws java.io.IOException {
    return ENCODER.encode(this);
  }

  /**
   * Deserializes a AvroStationCharacteristics from a ByteBuffer.
   * @param b a byte buffer holding serialized data for an instance of this class
   * @return a AvroStationCharacteristics instance decoded from the given buffer
   * @throws java.io.IOException if the given bytes could not be deserialized into an instance of this class
   */
  public static AvroStationCharacteristics fromByteBuffer(
      java.nio.ByteBuffer b) throws java.io.IOException {
    return DECODER.decode(b);
  }

  @Deprecated public java.lang.CharSequence stationCode;
  @Deprecated public java.lang.CharSequence stationName;
  @Deprecated public int totalCapacity;
  @Deprecated public double latitude;
  @Deprecated public double longitude;

  /**
   * Default constructor.  Note that this does not initialize fields
   * to their default values from the schema.  If that is desired then
   * one should use <code>newBuilder()</code>.
   */
  public AvroStationCharacteristics() {}

  /**
   * All-args constructor.
   * @param stationCode The new value for stationCode
   * @param stationName The new value for stationName
   * @param totalCapacity The new value for totalCapacity
   * @param latitude The new value for latitude
   * @param longitude The new value for longitude
   */
  public AvroStationCharacteristics(java.lang.CharSequence stationCode, java.lang.CharSequence stationName, java.lang.Integer totalCapacity, java.lang.Double latitude, java.lang.Double longitude) {
    this.stationCode = stationCode;
    this.stationName = stationName;
    this.totalCapacity = totalCapacity;
    this.latitude = latitude;
    this.longitude = longitude;
  }

  public org.apache.avro.specific.SpecificData getSpecificData() { return MODEL$; }
  public org.apache.avro.Schema getSchema() { return SCHEMA$; }
  // Used by DatumWriter.  Applications should not call.
  public java.lang.Object get(int field$) {
    switch (field$) {
    case 0: return stationCode;
    case 1: return stationName;
    case 2: return totalCapacity;
    case 3: return latitude;
    case 4: return longitude;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }

  // Used by DatumReader.  Applications should not call.
  @SuppressWarnings(value="unchecked")
  public void put(int field$, java.lang.Object value$) {
    switch (field$) {
    case 0: stationCode = (java.lang.CharSequence)value$; break;
    case 1: stationName = (java.lang.CharSequence)value$; break;
    case 2: totalCapacity = (java.lang.Integer)value$; break;
    case 3: latitude = (java.lang.Double)value$; break;
    case 4: longitude = (java.lang.Double)value$; break;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }

  /**
   * Gets the value of the 'stationCode' field.
   * @return The value of the 'stationCode' field.
   */
  public java.lang.CharSequence getStationCode() {
    return stationCode;
  }


  /**
   * Sets the value of the 'stationCode' field.
   * @param value the value to set.
   */
  public void setStationCode(java.lang.CharSequence value) {
    this.stationCode = value;
  }

  /**
   * Gets the value of the 'stationName' field.
   * @return The value of the 'stationName' field.
   */
  public java.lang.CharSequence getStationName() {
    return stationName;
  }


  /**
   * Sets the value of the 'stationName' field.
   * @param value the value to set.
   */
  public void setStationName(java.lang.CharSequence value) {
    this.stationName = value;
  }

  /**
   * Gets the value of the 'totalCapacity' field.
   * @return The value of the 'totalCapacity' field.
   */
  public int getTotalCapacity() {
    return totalCapacity;
  }


  /**
   * Sets the value of the 'totalCapacity' field.
   * @param value the value to set.
   */
  public void setTotalCapacity(int value) {
    this.totalCapacity = value;
  }

  /**
   * Gets the value of the 'latitude' field.
   * @return The value of the 'latitude' field.
   */
  public double getLatitude() {
    return latitude;
  }


  /**
   * Sets the value of the 'latitude' field.
   * @param value the value to set.
   */
  public void setLatitude(double value) {
    this.latitude = value;
  }

  /**
   * Gets the value of the 'longitude' field.
   * @return The value of the 'longitude' field.
   */
  public double getLongitude() {
    return longitude;
  }


  /**
   * Sets the value of the 'longitude' field.
   * @param value the value to set.
   */
  public void setLongitude(double value) {
    this.longitude = value;
  }

  /**
   * Creates a new AvroStationCharacteristics RecordBuilder.
   * @return A new AvroStationCharacteristics RecordBuilder
   */
  public static velibstreaming.avro.record.AvroStationCharacteristics.Builder newBuilder() {
    return new velibstreaming.avro.record.AvroStationCharacteristics.Builder();
  }

  /**
   * Creates a new AvroStationCharacteristics RecordBuilder by copying an existing Builder.
   * @param other The existing builder to copy.
   * @return A new AvroStationCharacteristics RecordBuilder
   */
  public static velibstreaming.avro.record.AvroStationCharacteristics.Builder newBuilder(velibstreaming.avro.record.AvroStationCharacteristics.Builder other) {
    if (other == null) {
      return new velibstreaming.avro.record.AvroStationCharacteristics.Builder();
    } else {
      return new velibstreaming.avro.record.AvroStationCharacteristics.Builder(other);
    }
  }

  /**
   * Creates a new AvroStationCharacteristics RecordBuilder by copying an existing AvroStationCharacteristics instance.
   * @param other The existing instance to copy.
   * @return A new AvroStationCharacteristics RecordBuilder
   */
  public static velibstreaming.avro.record.AvroStationCharacteristics.Builder newBuilder(velibstreaming.avro.record.AvroStationCharacteristics other) {
    if (other == null) {
      return new velibstreaming.avro.record.AvroStationCharacteristics.Builder();
    } else {
      return new velibstreaming.avro.record.AvroStationCharacteristics.Builder(other);
    }
  }

  /**
   * RecordBuilder for AvroStationCharacteristics instances.
   */
  @org.apache.avro.specific.AvroGenerated
  public static class Builder extends org.apache.avro.specific.SpecificRecordBuilderBase<AvroStationCharacteristics>
    implements org.apache.avro.data.RecordBuilder<AvroStationCharacteristics> {

    private java.lang.CharSequence stationCode;
    private java.lang.CharSequence stationName;
    private int totalCapacity;
    private double latitude;
    private double longitude;

    /** Creates a new Builder */
    private Builder() {
      super(SCHEMA$);
    }

    /**
     * Creates a Builder by copying an existing Builder.
     * @param other The existing Builder to copy.
     */
    private Builder(velibstreaming.avro.record.AvroStationCharacteristics.Builder other) {
      super(other);
      if (isValidValue(fields()[0], other.stationCode)) {
        this.stationCode = data().deepCopy(fields()[0].schema(), other.stationCode);
        fieldSetFlags()[0] = other.fieldSetFlags()[0];
      }
      if (isValidValue(fields()[1], other.stationName)) {
        this.stationName = data().deepCopy(fields()[1].schema(), other.stationName);
        fieldSetFlags()[1] = other.fieldSetFlags()[1];
      }
      if (isValidValue(fields()[2], other.totalCapacity)) {
        this.totalCapacity = data().deepCopy(fields()[2].schema(), other.totalCapacity);
        fieldSetFlags()[2] = other.fieldSetFlags()[2];
      }
      if (isValidValue(fields()[3], other.latitude)) {
        this.latitude = data().deepCopy(fields()[3].schema(), other.latitude);
        fieldSetFlags()[3] = other.fieldSetFlags()[3];
      }
      if (isValidValue(fields()[4], other.longitude)) {
        this.longitude = data().deepCopy(fields()[4].schema(), other.longitude);
        fieldSetFlags()[4] = other.fieldSetFlags()[4];
      }
    }

    /**
     * Creates a Builder by copying an existing AvroStationCharacteristics instance
     * @param other The existing instance to copy.
     */
    private Builder(velibstreaming.avro.record.AvroStationCharacteristics other) {
      super(SCHEMA$);
      if (isValidValue(fields()[0], other.stationCode)) {
        this.stationCode = data().deepCopy(fields()[0].schema(), other.stationCode);
        fieldSetFlags()[0] = true;
      }
      if (isValidValue(fields()[1], other.stationName)) {
        this.stationName = data().deepCopy(fields()[1].schema(), other.stationName);
        fieldSetFlags()[1] = true;
      }
      if (isValidValue(fields()[2], other.totalCapacity)) {
        this.totalCapacity = data().deepCopy(fields()[2].schema(), other.totalCapacity);
        fieldSetFlags()[2] = true;
      }
      if (isValidValue(fields()[3], other.latitude)) {
        this.latitude = data().deepCopy(fields()[3].schema(), other.latitude);
        fieldSetFlags()[3] = true;
      }
      if (isValidValue(fields()[4], other.longitude)) {
        this.longitude = data().deepCopy(fields()[4].schema(), other.longitude);
        fieldSetFlags()[4] = true;
      }
    }

    /**
      * Gets the value of the 'stationCode' field.
      * @return The value.
      */
    public java.lang.CharSequence getStationCode() {
      return stationCode;
    }


    /**
      * Sets the value of the 'stationCode' field.
      * @param value The value of 'stationCode'.
      * @return This builder.
      */
    public velibstreaming.avro.record.AvroStationCharacteristics.Builder setStationCode(java.lang.CharSequence value) {
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
    public velibstreaming.avro.record.AvroStationCharacteristics.Builder clearStationCode() {
      stationCode = null;
      fieldSetFlags()[0] = false;
      return this;
    }

    /**
      * Gets the value of the 'stationName' field.
      * @return The value.
      */
    public java.lang.CharSequence getStationName() {
      return stationName;
    }


    /**
      * Sets the value of the 'stationName' field.
      * @param value The value of 'stationName'.
      * @return This builder.
      */
    public velibstreaming.avro.record.AvroStationCharacteristics.Builder setStationName(java.lang.CharSequence value) {
      validate(fields()[1], value);
      this.stationName = value;
      fieldSetFlags()[1] = true;
      return this;
    }

    /**
      * Checks whether the 'stationName' field has been set.
      * @return True if the 'stationName' field has been set, false otherwise.
      */
    public boolean hasStationName() {
      return fieldSetFlags()[1];
    }


    /**
      * Clears the value of the 'stationName' field.
      * @return This builder.
      */
    public velibstreaming.avro.record.AvroStationCharacteristics.Builder clearStationName() {
      stationName = null;
      fieldSetFlags()[1] = false;
      return this;
    }

    /**
      * Gets the value of the 'totalCapacity' field.
      * @return The value.
      */
    public int getTotalCapacity() {
      return totalCapacity;
    }


    /**
      * Sets the value of the 'totalCapacity' field.
      * @param value The value of 'totalCapacity'.
      * @return This builder.
      */
    public velibstreaming.avro.record.AvroStationCharacteristics.Builder setTotalCapacity(int value) {
      validate(fields()[2], value);
      this.totalCapacity = value;
      fieldSetFlags()[2] = true;
      return this;
    }

    /**
      * Checks whether the 'totalCapacity' field has been set.
      * @return True if the 'totalCapacity' field has been set, false otherwise.
      */
    public boolean hasTotalCapacity() {
      return fieldSetFlags()[2];
    }


    /**
      * Clears the value of the 'totalCapacity' field.
      * @return This builder.
      */
    public velibstreaming.avro.record.AvroStationCharacteristics.Builder clearTotalCapacity() {
      fieldSetFlags()[2] = false;
      return this;
    }

    /**
      * Gets the value of the 'latitude' field.
      * @return The value.
      */
    public double getLatitude() {
      return latitude;
    }


    /**
      * Sets the value of the 'latitude' field.
      * @param value The value of 'latitude'.
      * @return This builder.
      */
    public velibstreaming.avro.record.AvroStationCharacteristics.Builder setLatitude(double value) {
      validate(fields()[3], value);
      this.latitude = value;
      fieldSetFlags()[3] = true;
      return this;
    }

    /**
      * Checks whether the 'latitude' field has been set.
      * @return True if the 'latitude' field has been set, false otherwise.
      */
    public boolean hasLatitude() {
      return fieldSetFlags()[3];
    }


    /**
      * Clears the value of the 'latitude' field.
      * @return This builder.
      */
    public velibstreaming.avro.record.AvroStationCharacteristics.Builder clearLatitude() {
      fieldSetFlags()[3] = false;
      return this;
    }

    /**
      * Gets the value of the 'longitude' field.
      * @return The value.
      */
    public double getLongitude() {
      return longitude;
    }


    /**
      * Sets the value of the 'longitude' field.
      * @param value The value of 'longitude'.
      * @return This builder.
      */
    public velibstreaming.avro.record.AvroStationCharacteristics.Builder setLongitude(double value) {
      validate(fields()[4], value);
      this.longitude = value;
      fieldSetFlags()[4] = true;
      return this;
    }

    /**
      * Checks whether the 'longitude' field has been set.
      * @return True if the 'longitude' field has been set, false otherwise.
      */
    public boolean hasLongitude() {
      return fieldSetFlags()[4];
    }


    /**
      * Clears the value of the 'longitude' field.
      * @return This builder.
      */
    public velibstreaming.avro.record.AvroStationCharacteristics.Builder clearLongitude() {
      fieldSetFlags()[4] = false;
      return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public AvroStationCharacteristics build() {
      try {
        AvroStationCharacteristics record = new AvroStationCharacteristics();
        record.stationCode = fieldSetFlags()[0] ? this.stationCode : (java.lang.CharSequence) defaultValue(fields()[0]);
        record.stationName = fieldSetFlags()[1] ? this.stationName : (java.lang.CharSequence) defaultValue(fields()[1]);
        record.totalCapacity = fieldSetFlags()[2] ? this.totalCapacity : (java.lang.Integer) defaultValue(fields()[2]);
        record.latitude = fieldSetFlags()[3] ? this.latitude : (java.lang.Double) defaultValue(fields()[3]);
        record.longitude = fieldSetFlags()[4] ? this.longitude : (java.lang.Double) defaultValue(fields()[4]);
        return record;
      } catch (org.apache.avro.AvroMissingFieldException e) {
        throw e;
      } catch (java.lang.Exception e) {
        throw new org.apache.avro.AvroRuntimeException(e);
      }
    }
  }

  @SuppressWarnings("unchecked")
  private static final org.apache.avro.io.DatumWriter<AvroStationCharacteristics>
    WRITER$ = (org.apache.avro.io.DatumWriter<AvroStationCharacteristics>)MODEL$.createDatumWriter(SCHEMA$);

  @Override public void writeExternal(java.io.ObjectOutput out)
    throws java.io.IOException {
    WRITER$.write(this, SpecificData.getEncoder(out));
  }

  @SuppressWarnings("unchecked")
  private static final org.apache.avro.io.DatumReader<AvroStationCharacteristics>
    READER$ = (org.apache.avro.io.DatumReader<AvroStationCharacteristics>)MODEL$.createDatumReader(SCHEMA$);

  @Override public void readExternal(java.io.ObjectInput in)
    throws java.io.IOException {
    READER$.read(this, SpecificData.getDecoder(in));
  }

  @Override protected boolean hasCustomCoders() { return true; }

  @Override public void customEncode(org.apache.avro.io.Encoder out)
    throws java.io.IOException
  {
    out.writeString(this.stationCode);

    out.writeString(this.stationName);

    out.writeInt(this.totalCapacity);

    out.writeDouble(this.latitude);

    out.writeDouble(this.longitude);

  }

  @Override public void customDecode(org.apache.avro.io.ResolvingDecoder in)
    throws java.io.IOException
  {
    org.apache.avro.Schema.Field[] fieldOrder = in.readFieldOrderIfDiff();
    if (fieldOrder == null) {
      this.stationCode = in.readString(this.stationCode instanceof Utf8 ? (Utf8)this.stationCode : null);

      this.stationName = in.readString(this.stationName instanceof Utf8 ? (Utf8)this.stationName : null);

      this.totalCapacity = in.readInt();

      this.latitude = in.readDouble();

      this.longitude = in.readDouble();

    } else {
      for (int i = 0; i < 5; i++) {
        switch (fieldOrder[i].pos()) {
        case 0:
          this.stationCode = in.readString(this.stationCode instanceof Utf8 ? (Utf8)this.stationCode : null);
          break;

        case 1:
          this.stationName = in.readString(this.stationName instanceof Utf8 ? (Utf8)this.stationName : null);
          break;

        case 2:
          this.totalCapacity = in.readInt();
          break;

        case 3:
          this.latitude = in.readDouble();
          break;

        case 4:
          this.longitude = in.readDouble();
          break;

        default:
          throw new java.io.IOException("Corrupt ResolvingDecoder.");
        }
      }
    }
  }
}










