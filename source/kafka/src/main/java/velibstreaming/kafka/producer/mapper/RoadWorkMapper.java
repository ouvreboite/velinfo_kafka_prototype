package velibstreaming.kafka.producer.mapper;

import velibstreaming.avro.record.AvroRoadWork;
import velibstreaming.avro.record.PerturbationLevel;
import velibstreaming.avro.record.Status;
import velibstreaming.opendata.dto.RoadWork;

public class RoadWorkMapper implements AvroMapper<RoadWork.Fields, AvroRoadWork> {
    @Override
    public AvroRoadWork map(RoadWork.Fields record) {
        return AvroRoadWork.newBuilder()
                .setId(record.getIdentifiant())
                .setPerturbationLevel(record.getNiveau_perturbation() == null ? PerturbationLevel.NONE : PerturbationLevel.valueOf(record.getNiveau_perturbation().name()))
                .setStatus(record.getStatut() == null ? Status.NONE : Status.valueOf(record.getStatut().name()))
                .setLatitude(record.getGeo_point_2d() != null ? record.getGeo_point_2d()[0] : 0.0)
                .setLongitude(record.getGeo_point_2d() != null ? record.getGeo_point_2d()[1] : 0.0)
                .setObject(record.getObjet())
                .setImpactDetails(record.getImpact_circulation_detail())
                .setStreetName(record.getVoie())
                .setStartTimestamp(record.getDate_debut().getTime())
                .setEndTimestamp(record.getDate_fin().getTime())
                .build();
    }
}
