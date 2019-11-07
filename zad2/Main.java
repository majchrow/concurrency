class Sem {
    private boolean up;

    public Sem() {
        this.up = false;
    }

    public void P(){
        synchronized (this){
            while (this.up){
                try{
                    this.wait();
                }catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    e.printStackTrace();
                }
            }
            this.up = true;
        }
    }

    public void V(){
        synchronized (this){
            while (!this.up){
                try{
                    this.wait();
                }catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    e.printStackTrace();
                }
            }
            this.up = false;
            this.notify();
        }
    }
}

class Counter {

    private int N;

    public Counter(int N) {
        this.N = N;
    }

    void inc() {
        this.N++;
    }

    void dec() {
        this.N--;
    }

    void print(){
        System.out.println("Current counter " + N);
    }

}

class thr implements Runnable{

    private Sem sem;
    private Counter cnt;
    private boolean increment;

    public thr(Sem sem, Counter counter, boolean increment) {
        this.sem = sem;
        this.cnt = counter;
        this.increment = increment;
    }

    @Override
    public void run(){
        for (int i = 0; i < 1000000; ++i) {
            sem.P();
            if (increment) {
                cnt.inc();
            } else {
                cnt.dec();
            }
            sem.V();
        }
    }
}

public class Main {
    public static void main(String[] args) throws InterruptedException{
        Counter counter = new Counter(0);
        Sem sem = new Sem();
        Runnable runnable1 = new thr(sem, counter, true);
        Runnable runnable2 = new thr(sem, counter, false);
        Runnable runnable3 = new thr(sem, counter, false);
        Thread thread1 = new Thread(runnable1);
        Thread thread2 = new Thread(runnable2);
        Thread thread3 = new Thread(runnable3);
        thread1.start();
        thread2.start();
        thread3.start();
        thread1.join();
        thread2.join();
        thread3.join();
        counter.print();
    }
}
