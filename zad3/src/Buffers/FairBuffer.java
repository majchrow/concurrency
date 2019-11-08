package Buffers;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class FairBuffer implements Buffer{
    private Lock lock;              // buffer access
    private Condition firstProd;
    private Condition restProd;
    private Condition firstCons;
    private Condition restCons;
    private Integer bufferSize;
    private Integer currentSize;
    private Boolean insideProd;
    private Boolean insideCons;

    public FairBuffer(Integer bufferSize) {
        this.bufferSize = bufferSize;
        this.currentSize = 0;
        this.lock = new ReentrantLock();
        this.firstProd = lock.newCondition();
        this.restProd  = lock.newCondition();
        this.firstCons = lock.newCondition();
        this.restCons  = lock.newCondition();
        this.insideProd = false;
        this.insideCons = false;
    }

    @Override
    public void put(Integer size){ // produce
        assert 2*size <= this.bufferSize;
        lock.lock();
        try{
            if(insideProd) restProd.await();
            insideProd = true;
            while(this.currentSize + size > this.bufferSize) firstProd.await();
            this.currentSize += size;
            insideProd = false;
            restProd.signal();
            firstCons.signal();
        }catch (InterruptedException e) { e.printStackTrace(); }
        finally { lock.unlock(); }
    }

    @Override
    public void get(Integer size){ // consume
        assert 2*size <= this.bufferSize;
        lock.lock();
        try{
            if(insideCons) restCons.await();
            insideCons = true;
            while(this.currentSize - size < 0) firstCons.await();
            this.currentSize -= size;
            insideCons = false;
            restCons.signal();
            firstProd.signal();
        }catch (InterruptedException e) { e.printStackTrace(); }
        finally { lock.unlock(); }
    }

}


