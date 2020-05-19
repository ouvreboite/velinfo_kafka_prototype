package velibstreaming.producer.client;

import kong.unirest.Unirest;

public abstract class OpenDataClient<Data> {
    private Class<Data> dataClass;
    private String urlPath;
    public OpenDataClient(Class<Data> dataClass, String urlPath){
        this.dataClass = dataClass;
        this.urlPath = urlPath;
    }

    public Data fetch(){
        return Unirest.get(urlPath)
                .header("accept", "application/json")
                .asObject(dataClass)
                .ifFailure(Error.class, r -> {
                    throw r.getBody();
                })
                .getBody();
    }
}
