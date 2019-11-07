import Buffers.Buffer;
import Buffers.UnfairBuffer;

import java.util.ArrayList;
import java.util.List;

public class Consumer implements Runnable{

    private Buffer buffer;
    private Integer bufferSize;
    private String randomization;
    private Integer config;
    private Integer size;
    private Long  startTime;

    public Consumer(Buffer buffer, Integer bufferSize, String randomization, Integer config, Integer size) {
        this.buffer = buffer;
        this.bufferSize = bufferSize;
        this.randomization = randomization;
        this.config = config;
        this.size = size;
        this.startTime = 0L;
    }

    @Override
    public void run() {
        while (true){
            this.startTime = System.nanoTime();
            buffer.get(size);
            System.out.println("Duration: " + (System.nanoTime()-this.startTime) + "BufferSize " + this.bufferSize + "Randomization " + this.randomization);
        }
    }


}
