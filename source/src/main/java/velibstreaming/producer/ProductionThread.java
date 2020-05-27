package velibstreaming.producer;

import velibstreaming.producer.client.OpenDataClient;
import velibstreaming.producer.client.dto.OpenDataDto;

import java.util.function.Supplier;

public class ProductionThread<T extends OpenDataDto<F>, F> extends Thread{
    private final long loopPeriodSeconds;
    private final OpenDataClient<T> client;
    private final Producer<T, F> producer;

    public ProductionThread(long loopPeriodSeconds, OpenDataClient<T> client, Producer<T,F> producer) {
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
                    producer.send(result);
                }catch(OpenDataClient.OpenDataException e){
                    System.err.println(e);
                }

                Thread.sleep(loopPeriodSeconds*1_000);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public ProductionThread<T,F> withParameter(String parameter, Supplier<String> valueSupplier){
        this.client.withParameter(parameter, valueSupplier.get());
        return this;
    }
}
