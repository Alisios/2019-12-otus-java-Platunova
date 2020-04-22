package ru.otus.jmm;
import java.util.Arrays;

class MessagePrint {
    private volatile boolean firstTimeIn = true;
    synchronized void call(int i,boolean firstKey)  {
        try{
            if (firstTimeIn){
                if  (!firstKey)  wait();
                firstTimeIn = false;
            }
            System.out.println(Thread.currentThread().getName()+" : "+i);
            Thread.sleep(100);
            notify();
            wait();
        }
        catch (InterruptedException ex){
            System.out.println("Exception in call: "+Arrays.toString(ex.getStackTrace()));
        }
    }
}
