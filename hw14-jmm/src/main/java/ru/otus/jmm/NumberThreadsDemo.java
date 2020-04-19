package ru.otus.jmm;

public class NumberThreadsDemo {
    public static void main(String[] args) {
        var number = new MessagePrint();
        new NumberThread(number,"Поток №1");
        new NumberThread(number, "Поток №2");
    }
}
