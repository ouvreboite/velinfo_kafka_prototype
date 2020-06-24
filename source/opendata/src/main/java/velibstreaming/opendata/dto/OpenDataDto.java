package velibstreaming.opendata.dto;

import lombok.Data;

import java.util.List;

@Data
public abstract class OpenDataDto<Fields> {
    private List<Record<Fields>> records;

    @Data
    public static class Record<Fields>{
        private Fields fields;
    }
}
