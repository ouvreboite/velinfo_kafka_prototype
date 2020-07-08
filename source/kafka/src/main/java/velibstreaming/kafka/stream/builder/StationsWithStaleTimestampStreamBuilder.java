package velibstreaming.kafka.stream.builder;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.KStream;
import velibstreaming.avro.record.stream.AvroStationChange;
import velibstreaming.kafka.stream.StreamUtils;

public class StationsWithStaleTimestampStreamBuilder {

    public KStream<String, AvroStationChange> build(KStream<String, AvroStationChange> stationChangesStream){
        return stationChangesStream
                .groupByKey(Grouped.with(Serdes.String(), StreamUtils.AvroSerde()))
                .reduce(this::KeepNewestStationAndCheckIfNumberOfBikesIsTheSame)
                .toStream();
    }

    protected AvroStationChange KeepNewestStationAndCheckIfNumberOfBikesIsTheSame(AvroStationChange stationV1, AvroStationChange stationV2) {
        var newest = stationV1.getLoadTimestamp()>stationV2.getLoadTimestamp() ? stationV1 : stationV2;
        var oldest = stationV1.getLoadTimestamp()<stationV2.getLoadTimestamp() ? stationV1 : stationV2;

        if(newest.getElectricBikesAtStation() == oldest.getElectricBikesAtStation()
                && newest.getMechanicalBikesAtStation() == oldest.getMechanicalBikesAtStation()){
            var oldestStaleTimestamp = oldest.getStaleSinceTimestamp() == null ? oldest.getLoadTimestamp() : oldest.getStaleSinceTimestamp();
            newest.setStaleSinceTimestamp(oldestStaleTimestamp);
        }
        return newest;
    }
}
