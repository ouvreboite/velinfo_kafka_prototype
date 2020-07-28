package velibstreaming.opendata.client;

import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import io.vavr.control.Try;
import kong.unirest.Unirest;
import velibstreaming.opendata.dto.OpenDataDto;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

public abstract class OpenDataClient<Data extends OpenDataDto> {
    public static final String ROW_COUNT_PARAMETER = "rows";
    public static final int ROW_COUNT_PARAMETER_MAX_VALUE = 10_000;
    public static final String BASE_PATH = "https://opendata.paris.fr/api/records/1.0/search/?dataset=";

    private final Class<Data> dataClass;
    private final String urlPath;
    private final Map<String, String> parameters;
    private final Retry retry;

    public OpenDataClient(Class<Data> dataClass, String datasetName){
        this.dataClass = dataClass;
        this.urlPath = BASE_PATH+datasetName;
        this.parameters = new HashMap<>();
        this.retry = Retry.of("openDataClient for "+dataClass, RetryConfig.custom()
                .maxAttempts(3)
                .waitDuration(Duration.ofSeconds(10))
                .build());
        this.withParameter(ROW_COUNT_PARAMETER, ROW_COUNT_PARAMETER_MAX_VALUE);
    }

    public OpenDataClient<Data> withParameter(String parameter, Object value){
        this.parameters.put(parameter, value.toString());
        return this;
    }

    public Data get() throws OpenDataException{
        String completePath = getCompletePath();

        var retriableGet = Retry.decorateCheckedSupplier(this.retry, () -> get(completePath));

        return Try.of(retriableGet).getOrElseThrow(e -> new OpenDataException("Error when calling "+completePath, e));
    }

    private Data get(String completePath) {
        System.out.println(completePath);
        return Unirest.get(completePath)
                .header("accept", "application/json")
                .asObject(dataClass)
                .ifFailure(Error.class, r -> {throw r.getBody();})
                .getBody();
    }

    private String getCompletePath() {
        String parametersString = parameters.entrySet().stream()
                .map(e -> "&"+e.getKey() + "=" + e.getValue())
                .reduce((p1, p2) -> p1 + p2)
                .orElse("");
        return urlPath + parametersString;
    }

    public static class OpenDataException extends Exception{
        public OpenDataException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
