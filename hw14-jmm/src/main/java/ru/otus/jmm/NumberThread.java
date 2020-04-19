package ru.otus.jmm;

public class NumberThread implements Runnable{
    private MessagePrint number;

    NumberThread(MessagePrint number, String threadName){
        this.number = number;
        new Thread(this, threadName).start();
    }

    @Override
    public void run() {
        int i = 0;
        boolean flag = true;
        while (true){
            if (i==1) flag=true;
            if (i==10) flag = false;
            if (i < 11 && flag) i++;
            else i--;
            number.call(i);
        }
    }
}
