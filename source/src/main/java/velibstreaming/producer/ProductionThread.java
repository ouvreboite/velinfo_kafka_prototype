package velibstreaming.producer;

import org.apache.avro.specific.SpecificRecord;
import velibstreaming.producer.client.OpenDataClient;
import velibstreaming.producer.client.dto.OpenDataDto;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public class ProductionThread<T extends OpenDataDto<F>, F, A extends SpecificRecord> extends Thread{
    private final long loopPeriodSeconds;
    private final OpenDataClient<T> client;
    private final Producer<T, F, A> producer;

    public ProductionThread(long loopPeriodSeconds, OpenDataClient<T> client, Producer<T,F, A> producer) {
        super();
        this.loopPeriodSeconds = loopPeriodSeconds;
        this.client = client;
        this.producer = producer;
        this.setDaemon(true);
        System.out.println("Fetching data from : "+client.getClass()+" every "+loopPeriodSeconds+" seconds");
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

                TimeUnit.SECONDS.sleep(loopPeriodSeconds);
            }
        } catch (InterruptedException e) {
            System.err.println(e);
        }
    }

    public ProductionThread<T,F,A> withParameter(String parameter, Supplier<String> valueSupplier){
        this.client.withParameter(parameter, valueSupplier.get());
        return this;
    }
}
