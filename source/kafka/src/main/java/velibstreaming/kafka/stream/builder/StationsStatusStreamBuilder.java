package velibstreaming.kafka.stream.builder;

import org.apache.kafka.streams.kstream.*;
import velibstreaming.avro.record.stream.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

public class StationsStatusStreamBuilder {

    public KStream<String, AvroStationStatus> build(KStream<String, AvroStationUpdate> stationUpdatesStream){
        return stationUpdatesStream.mapValues((s, station) -> {
            StationStatus officialStatus = GetOfficialStatus(station);
            if(officialStatus != StationStatus.OK){
                return new AvroStationStatus(station.getStationCode(), officialStatus, null);
            }

            if(staleForTooLong(station.getStaleSinceTimestamp(), station.getStationCode())){
                return new AvroStationStatus(station.getStationCode(), StationStatus.STALE, station.getStaleSinceTimestamp());
            }

            return new AvroStationStatus(station.getStationCode(), StationStatus.OK, null);
        });
    }

    private boolean staleForTooLong(Long staleSinceTimestamp, String stationCode) {
        if(staleSinceTimestamp == null)
            return false;

        LocalDateTime staleSince = LocalDateTime.ofInstant(Instant.ofEpochMilli(staleSinceTimestamp), ZoneId.systemDefault());
        return staleSince.until(LocalDateTime.now(), ChronoUnit.HOURS) > 6;
    }

    private StationStatus GetOfficialStatus(AvroStationUpdate station) {
        StationStatus officialStatus = StationStatus.OK;
        if(!station.getIsInstalled()){
            officialStatus =  StationStatus.NOT_INSTALLED;
        }
        else if(!station.getIsRenting() && !station.getIsReturning()){
            officialStatus =  StationStatus.NOT_RENTING_NOR_RETURNING;
        }
        else if(!station.getIsRenting()){
            officialStatus =  StationStatus.NOT_RENTING;
        }
        else if(!station.getIsReturning()){
            officialStatus =  StationStatus.NOT_RETURNING;
        }
        return officialStatus;
    }

}
