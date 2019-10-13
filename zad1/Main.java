class Counter {

    private int toPrint;
    private int N;

    public Counter(int toPrint, int N) {
        this.toPrint = toPrint;
        this.N = N;
    }

    void increment() {
        this.toPrint = (this.toPrint + 1) % this.N;
    }

    int getToPrint() {
        return toPrint;
    }
}


class Printer implements Runnable {

    private int iterCount;
    private int number;
    private Counter counter;

    public Printer(int iterCount, int number, Counter counter) {
        this.iterCount = iterCount;
        this.number = number;
        this.counter = counter;
    }

    @Override
    public void run() {
        for (int i = 0; i < iterCount; ++i) {
            synchronized (this.counter) {
                while (this.number != counter.getToPrint()) {
                    try {
                        counter.wait();

                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        e.printStackTrace();
                    }
                }
            }

            System.out.println("Thread " + this.number + " printing " + this.counter.getToPrint());
            this.counter.increment();
            synchronized (this.counter) {
                this.counter.notifyAll();
            }
        }
    }
}

public class Main {
    public static int N = 10;

    public static void main(String[] args) throws InterruptedException {
        Runnable runnables[] = new Runnable[N];
        Thread threads[] = new Thread[N];
        Counter counter = new Counter(0, N);
        for (int i = 0; i < N; ++i) {
            runnables[i] = new Printer(10, i, counter);
            threads[i] = new Thread(runnables[i]);
        }
        for (int i = 0; i < N; ++i) {
            threads[i].start();
        }
        for (int i = 0; i < N; ++i) {
            threads[i].join();
        }
    }
} 