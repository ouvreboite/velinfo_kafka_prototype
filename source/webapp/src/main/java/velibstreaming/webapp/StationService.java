package velibstreaming.webapp;

import velibstreaming.avro.record.AvroRealTimeAvailability;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class StationService {

    public List<AvroRealTimeAvailability> getStations(){
        return Arrays.asList(
                AvroRealTimeAvailability.newBuilder()
                        .setStationCode("A")
                        .setElectricBikesAtStation(1)
                        .setMechanicalBikesAtStation(12)
                        .setAvailabilityTimestamp(new Date().getTime())
                        .build(),
                AvroRealTimeAvailability.newBuilder()
                        .setStationCode("B")
                        .setElectricBikesAtStation(10)
                        .setMechanicalBikesAtStation(0)
                        .setAvailabilityTimestamp(new Date().getTime())
                        .build(),
                AvroRealTimeAvailability.newBuilder()
                        .setStationCode("C")
                        .setElectricBikesAtStation(5)
                        .setMechanicalBikesAtStation(4)
                        .setAvailabilityTimestamp(new Date().getTime())
                        .build()
        );
    }
}
