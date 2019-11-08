package Threads;
import Buffers.Buffer;

public class ProdCons implements Runnable{

    private String prodOrCons;
    private Buffer buffer;
    private Integer bufferSize;
    private String randomization;
    private Boolean isFair;
    private Integer config;
    private Integer size;
    private Long startTime;

    public ProdCons(Buffer buffer, Integer bufferSize, String prodOrCons, Integer size, Integer config, Boolean isFair, String randomization) {
        this.buffer = buffer;
        this.bufferSize = 2*bufferSize;
        this.prodOrCons = prodOrCons;
        this.size = size;
        this.config = config;
        this.isFair = isFair;
        this.randomization = randomization;
        this.startTime = 0L;
    }

    @Override
    public void run() {
        if (prodOrCons.equals("Prod")){
            while (true) {
                this.startTime = System.nanoTime();
                buffer.get(size);
                System.out.println((this.bufferSize / 2) + ",Prod," + this.size + "," + this.config + "," + this.isFair + "," + this.randomization + "," + (System.nanoTime() - this.startTime));
            }
        }else if(prodOrCons.equals("Cons")){
            this.startTime = System.nanoTime();
            buffer.put(size);
            System.out.println((this.bufferSize / 2) + ",Cons," + this.size + "," + this.config + "," + this.isFair + "," + this.randomization + "," + (System.nanoTime() - this.startTime));
        }else {
            throw new RuntimeException("Wrong value for prodOrCons");
        }
    }

}
