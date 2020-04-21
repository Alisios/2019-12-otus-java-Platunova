package ru.otus.jmm;

public class NumberThreadsDemo {
    public static void main(String[] args) {
        var number = new MessagePrint();
        Thread myThread1 = new Thread(new NumberThread(number, "Поток №1", true));
        myThread1.start();
        Thread myThread2 = new Thread(new NumberThread(number, "Поток №2", false));
        myThread2.start();
    }
}

