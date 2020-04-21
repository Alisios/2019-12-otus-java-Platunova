package ru.otus.jmm;

public class NumberThread implements Runnable{
    private MessagePrint number;
    private String threadName;
    private boolean firstKey;

    NumberThread(MessagePrint number, String threadName, boolean firstKey){
        this.number = number;
        this.threadName = threadName;
        this.firstKey = firstKey;
    }

    @Override
    public void run() {
        Thread.currentThread().setName(threadName);
        int i = 0;
        boolean flag = true;
        while (true) {
            if (i == 1) flag = true;
            if (i == 10) flag = false;
            if (i < 11 && flag) i++;
            else i--;
            number.call(i, firstKey);
        }
    }
}
