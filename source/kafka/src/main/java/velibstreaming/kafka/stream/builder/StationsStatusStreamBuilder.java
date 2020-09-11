package velibstreaming.kafka.stream.builder;

import org.apache.kafka.streams.kstream.KStream;
import velibstreaming.avro.record.stream.AvroStationStatus;
import velibstreaming.avro.record.stream.AvroStationUpdate;
import velibstreaming.avro.record.stream.StationStatus;
import velibstreaming.kafka.stream.builder.lock.ExpectedActivityCalculator;
import velibstreaming.kafka.stream.builder.lock.LockedStationDetector;
import velibstreaming.repository.HourlyStationStatsRepository;

public class StationsStatusStreamBuilder {
    private final LockedStationDetector lockedStationDetector = new LockedStationDetector(new HourlyStationStatsRepository(), new ExpectedActivityCalculator());

    public KStream<String, AvroStationStatus> build(KStream<String, AvroStationUpdate> stationUpdatesStream){
        return stationUpdatesStream.mapValues((s, update) -> {
            StationStatus officialStatus = GetOfficialStatus(update);
            if(officialStatus != StationStatus.OK){
                return new AvroStationStatus(update.getStationCode(), officialStatus, update.getLastMovementTimestamp());
            }

            if(lockedStationDetector.isStationLocked(update)){
                return new AvroStationStatus(update.getStationCode(), StationStatus.LOCKED, update.getLastMovementTimestamp());
            }

            return new AvroStationStatus(update.getStationCode(), StationStatus.OK, update.getLastMovementTimestamp());
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
