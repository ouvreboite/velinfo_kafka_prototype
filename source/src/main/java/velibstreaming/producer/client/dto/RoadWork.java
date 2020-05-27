package velibstreaming.producer.client.dto;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.util.Date;

@Data
public class RoadWork extends OpenDataDto<RoadWork.Fields> {
    @Data
    public static class Fields {
        private String identifiant;
        private String objet;
        private PerturbationLevel niveau_perturbation;
        private String voie;
        private Status statut;
        private Date date_debut;
        private Date date_fin;
        private double[] geo_point_2d;
        private String impact_circulation_detail;
    }

    public enum Status{
        @SerializedName("1")
        TO_COME,
        @SerializedName("2")
        ONGOING,
        @SerializedName("3")
        HALTED,
        @SerializedName("4")
        EXTENDED,
        @SerializedName("5")
        FINISHED;
    }

    public enum PerturbationLevel{
        @SerializedName("2")
        VERY_DISTURBING,
        @SerializedName("1")
        DISTURBING;
    }
}
