package fr.velinfo.kafka.producer;

import org.apache.avro.specific.SpecificRecord;
import fr.velinfo.opendata.client.OpenDataClient;
import fr.velinfo.opendata.dto.OpenDataDto;

import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public class ProductionThread<T extends OpenDataDto<F>, F, A extends SpecificRecord> extends Thread{
    private final Duration loopDuration;
    private final OpenDataClient<T> client;
    private final Producer<T, F, A> producer;

    public ProductionThread(Duration loopDuration, OpenDataClient<T> client, Producer<T,F, A> producer) {
        super();
        this.loopDuration = loopDuration;
        this.client = client;
        this.producer = producer;
        this.setDaemon(true);
        System.out.println("Producing "+client.getClass()+" every "+humanReadableFormat(loopDuration));
    }

    private static String humanReadableFormat(Duration duration) {
        return duration.toString()
                .substring(2)
                .replaceAll("(\\d[HMS])(?!$)", "$1 ")
                .toLowerCase();
    }

    @Override
    public void run() {
        try {
            while(true){
                try{
                    T result = client.get();
                    System.out.println("Fetched "+result.getRecords().size()+" "+result.getClass());
                    if(!result.getRecords().isEmpty())
                        producer.send(result);
                }catch(OpenDataClient.OpenDataException e){
                    System.err.println(e);
                }

                TimeUnit.SECONDS.sleep(loopDuration.toSeconds());
            }
        } catch (InterruptedException e) {
            System.err.println(e);
        }
    }

    public ProductionThread<T,F,A> withParameter(String parameter, Supplier<String> valueSupplier){
        this.client.withParameter(parameter, valueSupplier);
        return this;
    }
}
