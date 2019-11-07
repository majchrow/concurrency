import Buffers.Buffer;
import Buffers.FairBuffer;
import Buffers.UnfairBuffer;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Generator implements Runnable{
    private Integer buffSize;
    private String prodOrCons;
    private Integer pkConfig;
    private boolean isFair;
    private String randomization;
    private Integer size;

    public Generator(Integer buffSize, String prodOrCons, Integer pkConfig, boolean isFair, String randomization) {
        this.buffSize = buffSize;
        this.prodOrCons = prodOrCons;
        this.pkConfig = pkConfig;
        this.isFair = isFair;
        this.randomization = randomization;
        this.size = this.getSize(buffSize, randomization);
    }

    @Override
    public void run() {
        System.out.println("Generator started!");
        Thread threads[] = new Thread[2*pkConfig];
        Buffer buffer = isFair? new FairBuffer() : new UnfairBuffer(buffSize);
        for(int i = 0; i < pkConfig; ++i){
            threads[2*i] = new Thread(new Consumer(buffer, 2*buffSize, randomization, pkConfig, getSize(buffSize, randomization))); // consumers
            threads[2*i+1] = new Thread(new Producer(buffer, 2*buffSize, randomization, pkConfig, getSize(buffSize, randomization))); // producers
        }
        for(int i = 0; i < 2*pkConfig; ++i){
            threads[i].start();
        }
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for(int i = 0; i < 2*pkConfig; ++i){
            threads[i].interrupt();
        }
    }

    public static Integer getSize(Integer bufsize, String randomization){
        if(randomization.equals("Equal")){
            return 100;
        }else if(randomization.equals("Unequal")){
            return 100;
        }else throw new RuntimeException("Wrong randomization arg");
    }
}

