package fr.velinfo.kafka.stream.builder.projection;

import fr.velinfo.avro.record.source.AvroCoordinates;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class GeoProjector {

    private final DecimalFormat oneHundredMeterPrecision = new DecimalFormat("00.000", new DecimalFormatSymbols(Locale.FRANCE));
    private static final double TWO_HUNDRED_METERS_DELTA = 0.002;

    public String get100MeterZone(AvroCoordinates coordinates) {
        return get100MeterZone(coordinates.getLatitude(), coordinates.getLongitude());
    }

    public List<String> get100MetersNearbyZones(AvroCoordinates coordinates) {
        double latitude = coordinates.getLatitude();
        double longitude = coordinates.getLongitude();

        List<String> closeByZones = new ArrayList<>();

        closeByZones.add(get100MeterZone(latitude+ TWO_HUNDRED_METERS_DELTA, longitude));
        closeByZones.add(get100MeterZone(latitude+ TWO_HUNDRED_METERS_DELTA, longitude+ TWO_HUNDRED_METERS_DELTA));
        closeByZones.add(get100MeterZone(latitude+ TWO_HUNDRED_METERS_DELTA, longitude- TWO_HUNDRED_METERS_DELTA));

        closeByZones.add(get100MeterZone(latitude, longitude));
        closeByZones.add(get100MeterZone(latitude, longitude+ TWO_HUNDRED_METERS_DELTA));
        closeByZones.add(get100MeterZone(latitude, longitude- TWO_HUNDRED_METERS_DELTA));

        closeByZones.add(get100MeterZone(latitude- TWO_HUNDRED_METERS_DELTA, longitude));
        closeByZones.add(get100MeterZone(latitude- TWO_HUNDRED_METERS_DELTA, longitude+ TWO_HUNDRED_METERS_DELTA));
        closeByZones.add(get100MeterZone(latitude- TWO_HUNDRED_METERS_DELTA, longitude- TWO_HUNDRED_METERS_DELTA));
        return closeByZones;
    }

    private String get100MeterZone(double latitude, double longitude) {
        return oneHundredMeterPrecision.format(round(latitude))+"_"+oneHundredMeterPrecision.format(round(longitude));
    }

    private double round(double coordinate){
        return TWO_HUNDRED_METERS_DELTA*(Math.round(coordinate/TWO_HUNDRED_METERS_DELTA));
    }
}
