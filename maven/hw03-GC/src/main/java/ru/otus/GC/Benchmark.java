package ru.otus.GC;

import java.util.*;

class Benchmark implements BenchmarkMXBean {
    private final int loopCounter;
    private volatile int size = 0;

   public class Stack
   {
       //static  List <String> list_for_res = ArrayList<>();
       private Object[] elements;
       private int size = 0;
       private static final int DEFAULT_INIT_CAPICITY = 16;

       public Stack(){
           elements = new Object [DEFAULT_INIT_CAPICITY];
       }
       public void push(Object e)
       {
           ensureCapacity();
           elements[size++]=e;
       }
       public Object pop()
       {
           if (size == 0)
               throw new EmptyStackException();
           return elements[--size];             //утечка памяти 1
       }
       private void ensureCapacity(){
           if (elements.length == size)
           {
               elements = Arrays.copyOf(elements,2*size+1);
           }
        }
   }

    public Benchmark( int loopCounter ) {
        this.loopCounter = loopCounter;
    }

    void run() throws InterruptedException {
        for (int idx = 0; idx < loopCounter; idx++) {
            Stack array = new Stack();
            int local = size;
            for (int i = 0; i < local; i++) {
                array.push(new String ("How"));  //утечка памяти 2
                //array.push("to");
                array.push("to cause");
                array.push("leak");
                array.push("of");
                array.pop();
            }
            array.push("memory");
           // Thread.sleep( 10 );
        }
    }

    @Override
    public void setSize( int size ) {
        System.out.println( "new size:" + size );
        this.size = size;
    }
}
