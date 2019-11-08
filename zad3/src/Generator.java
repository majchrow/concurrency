import Buffers.Buffer;
import Buffers.FairBuffer;
import Buffers.UnfairBuffer;
import Threads.ProdCons;

import java.util.Random;

public class Generator implements Runnable{
    private static final Random random = new Random();
    private Integer buffSize;
    private Integer pkConfig;
    private boolean isFair;
    private String randomization;

    public Generator(Integer buffSize, Integer pkConfig, boolean isFair, String randomization) {
        this.buffSize = buffSize;
        this.pkConfig = pkConfig;
        this.isFair = isFair;
        this.randomization = randomization;
    }

    @Override
    public void run() {
        Thread[] threads = new Thread[2*pkConfig];
        Buffer buffer = isFair ? new FairBuffer(2*buffSize) : new UnfairBuffer(2*buffSize);
        for(int i = 0; i < pkConfig; ++i){
            threads[2*i] =   new Thread(new ProdCons(buffer, buffSize, "Cons", getSize(buffSize, randomization), pkConfig, isFair, randomization)); // consumers
            threads[2*i+1] = new Thread(new ProdCons(buffer, buffSize, "Prod", getSize(buffSize, randomization), pkConfig, isFair, randomization)); // producers
        }
        for(int i = 0; i < 2*pkConfig; ++i){
            threads[i].start();
        }
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static Integer getSize(Integer buffSize, String randomization){
        if(randomization.equals("Equal")){
            return random.nextInt(buffSize) + 1;
        }else if(randomization.equals("Unequal")){
            int small = random.nextInt(10); // Higher chance of
            if(small < 6){ // 0-5
                return random.nextInt(buffSize/200) + 1;     // 50% for 50 or 500
            }
            if(small < 9){ // 0-8
                return random.nextInt(buffSize/100) + 1; // 30% for 100 or 1000
            }
            return random.nextInt(buffSize) + 1;           // 20% for normal randomization
        }else throw new RuntimeException("Wrong randomization arg");
    }
}

