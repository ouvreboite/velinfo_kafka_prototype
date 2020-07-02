package velibstreaming.kafka.stream;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.KStream;
import velibstreaming.avro.record.stream.AvroStation;

public class StationsWithStaleTimestampStreamBuilder {

    public KStream<String, AvroStation> build(KStream<String, AvroStation> stationChangesStream){
        return stationChangesStream
                .groupByKey(Grouped.with(Serdes.String(), StreamUtils.AvroSerde()))
                .reduce(this::KeepNewestStationAndCheckIfNumberOfBikesIsTheSame)
                .toStream();
    }

    protected AvroStation KeepNewestStationAndCheckIfNumberOfBikesIsTheSame(AvroStation stationV1, AvroStation stationV2) {
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
