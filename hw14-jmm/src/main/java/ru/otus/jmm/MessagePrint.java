package ru.otus.jmm;
import java.util.Arrays;

class MessagePrint {
    synchronized void call(int i){
        try{
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
