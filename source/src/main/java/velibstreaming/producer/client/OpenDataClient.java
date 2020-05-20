package velibstreaming.producer.client;

import kong.unirest.Unirest;

import java.util.HashMap;
import java.util.Map;

public abstract class OpenDataClient<Data> {
    public static final String ROW_COUNT_PARAMETER = "rows";
    public static final String ROW_COUNT_PARAMETER_MAX_VALUE = "10000";
    public static final String BASE_PATH = "https://opendata.paris.fr/api/records/1.0/search/?dataset=";

    private Class<Data> dataClass;
    private String urlPath;
    private Map<String, String> parameters;
    public OpenDataClient(Class<Data> dataClass, String datasetName){
        this.dataClass = dataClass;
        this.urlPath = BASE_PATH+datasetName;
        this.parameters = new HashMap<>();
        this.withParameter(ROW_COUNT_PARAMETER, ROW_COUNT_PARAMETER_MAX_VALUE);
    }

    public OpenDataClient<Data> withParameter(String parameter, String value){
        this.parameters.put(parameter, value);
        return this;
    }
    public Data get(){
        String completePath = getCompletePath();
        return Unirest.get(completePath)
                .header("accept", "application/json")
                .asObject(dataClass)
                .ifFailure(Error.class, r -> {
                    throw r.getBody();
                })
                .getBody();
    }

    private String getCompletePath() {
        String parametersString = parameters.entrySet().stream()
                .map(e -> "&"+e.getKey() + "=" + e.getValue())
                .reduce((p1, p2) -> p1 + p2)
                .orElse("");
        return urlPath + parametersString;
    }
}
