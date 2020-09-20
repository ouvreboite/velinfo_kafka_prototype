package fr.velinfo.kafka.stream.builder;

import org.apache.kafka.streams.kstream.KStream;
import fr.velinfo.avro.record.stream.AvroStationStatus;
import fr.velinfo.avro.record.stream.AvroStationUpdate;
import fr.velinfo.avro.record.stream.StationStatus;
import fr.velinfo.kafka.stream.builder.lock.ExpectedActivityCalculator;
import fr.velinfo.kafka.stream.builder.lock.LockedStationDetector;
import fr.velinfo.repository.HourlyStationStatsRepository;
import org.springframework.stereotype.Component;

@Component
public class StationsStatusStreamBuilder {
    private final LockedStationDetector lockedStationDetector;

    public StationsStatusStreamBuilder(LockedStationDetector lockedStationDetector) {
        this.lockedStationDetector = lockedStationDetector;
    }

    public KStream<String, AvroStationStatus> build(KStream<String, AvroStationUpdate> stationUpdatesStream){
        return stationUpdatesStream.mapValues((s, update) -> {
            StationStatus officialStatus = GetOfficialStatus(update);
            if(officialStatus != StationStatus.OK){
                return new AvroStationStatus(update.getStationCode(), officialStatus, update.getLastChangeTimestamp());
            }

            if(lockedStationDetector.isStationLocked(update)){
                return new AvroStationStatus(update.getStationCode(), StationStatus.LOCKED, update.getLastChangeTimestamp());
            }

            return new AvroStationStatus(update.getStationCode(), StationStatus.OK, update.getLastChangeTimestamp());
        });
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
