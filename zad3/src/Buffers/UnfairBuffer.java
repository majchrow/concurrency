package Buffers;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class UnfairBuffer implements Buffer{
    private Lock lock;              // buffer access
    private Condition underflow;    // prevents underflow
    private Condition overflow;     // prevents overflow
    private Integer bufferSize;
    private Integer currentSize;

    public UnfairBuffer(Integer bufferSize) {
        this.bufferSize = bufferSize;
        this.currentSize = 0;
        this.lock = new ReentrantLock();
        this.underflow  = lock.newCondition();
        this.overflow = lock.newCondition();
    }

    @Override
    public void put(Integer size){ // produce
        assert 2*size <= this.bufferSize;
        lock.lock();
        try{
            while(this.currentSize + size > this.bufferSize) overflow.await();
            this.currentSize += size;
            underflow.signalAll();
        }catch (InterruptedException e) { e.printStackTrace(); }
        finally { lock.unlock(); }
    }

    @Override
    public void get(Integer size){ // consume
        assert 2*size <= this.bufferSize;
        lock.lock();
        try{
            while(this.currentSize - size < 0) overflow.await();
            this.currentSize -= size;
            overflow.signalAll();
        }catch (InterruptedException e) { e.printStackTrace(); }
        finally { lock.unlock(); }
    }

}


