package ru.otus.GC;

import java.util.*;

class Benchmark implements BenchmarkMXBean {
    private final int loopCounter;
    private volatile int size = 0;

    public Benchmark(int loopCounter) {
        this.loopCounter = loopCounter;
    }

    void run()  {
        ArrayList<Integer> array = new ArrayList<>();
        array.add(3);
        array.add(6);
        for (int idx = 0; idx < loopCounter; idx++) {
            int local = size;
            for (int i = 1; i < local; i++)
                array.add(i);
            if ((local - (local >> 3)) > 1)
                array.subList(1, (local - (local >> 3))).clear();
        }
    }

    @Override
    public void setSize(int size) {
        System.out.println("new size:" + size);
        this.size = size;
    }
}
