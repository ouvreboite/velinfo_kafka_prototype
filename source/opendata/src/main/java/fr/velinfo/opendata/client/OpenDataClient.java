package fr.velinfo.opendata.client;

import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import io.vavr.control.Try;
import kong.unirest.Unirest;
import fr.velinfo.opendata.dto.OpenDataDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public abstract class OpenDataClient<Data extends OpenDataDto> {
    private static final Logger LOGGER = LoggerFactory.getLogger(OpenDataClient.class);
    public static final String ROW_COUNT_PARAMETER = "rows";
    public static final int ROW_COUNT_PARAMETER_MAX_VALUE = 10_000;
    public static final String BASE_PATH = "https://opendata.paris.fr/api/records/1.0/search/?dataset=";

    private final Class<Data> dataClass;
    private final String urlPath;
    private final Map<String, Supplier<String>> parameters;
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

    public OpenDataClient<Data> withParameter(String parameter, Supplier<String> valueSupplier){
        this.parameters.put(parameter, valueSupplier);
        return this;
    }

    public OpenDataClient<Data> withParameter(String parameter, Object value){
        this.parameters.put(parameter, value::toString);
        return this;
    }

    public Data get() throws OpenDataException{
        String completePath = getCompletePath();
        LOGGER.info("Calling {}", completePath);
        var retriableGet = Retry.decorateCheckedSupplier(this.retry, () -> get(completePath));

        return Try.of(retriableGet).getOrElseThrow(e -> new OpenDataException("Error when calling "+completePath, e));
    }

    private Data get(String completePath) {
        return Unirest.get(completePath)
                .header("accept", "application/json")
                .asObject(dataClass)
                .ifFailure(Error.class, r -> {throw r.getBody();})
                .getBody();
    }

    private String getCompletePath() {
        String parametersString = parameters.entrySet().stream()
                .map(e -> "&"+e.getKey() + "=" + e.getValue().get())
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
