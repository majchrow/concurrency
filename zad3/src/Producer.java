import Buffers.Buffer;
import Buffers.UnfairBuffer;

import java.util.ArrayList;
import java.util.List;

public class Producer implements Runnable{

    private Buffer buffer;
    private Integer bufferSize;
    private String randomization;
    private Integer config;
    private Integer size;
    private Long  startTime;
    private List<Long> durations;

    public Producer(Buffer buffer, Integer bufferSize, String randomization, Integer config, Integer size) {
        this.buffer = buffer;
        this.bufferSize = bufferSize;
        this.randomization = randomization;
        this.config = config;
        this.size = size;
        this.startTime = 0L;
        this.durations = new ArrayList<>();
    }

    @Override
    public void run() {
        while (true){
            this.startTime = System.nanoTime();
            buffer.put(size);
            this.durations.add(System.nanoTime()-this.startTime);
            System.out.print("Durations: " + this.durations);
        }
    }

}
