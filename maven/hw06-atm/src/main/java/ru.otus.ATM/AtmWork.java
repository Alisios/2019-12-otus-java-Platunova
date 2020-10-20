package ru.otus.ATM;

import java.util.ArrayList;
import java.util.Arrays;

public class AtmWork {
    public static void main(String[] args) {
        ArrayList<Integer> initialStateOfNominal = new ArrayList<>(Arrays.asList(100, 0, 30, 10, 5, 0, 2));
        var atm = new AtmProcessor(initialStateOfNominal);
        System.out.println("Присутсвующий в банкомате номинал:" + atm.getNominal());
        System.out.println("Количество купюр каждого номинала:" + atm.getNumberOfNominal());
        System.out.println("Баланс средств в банкомате: " + atm.balance());

        atm.withDrawMoney(17250);
        System.out.println("Баланс средств в банкомате после снятия: " + atm.balance());
        System.out.println("Присутсвующий в банкомате номинал:" + atm.getNominal());
        System.out.println("Количество купюр каждого номинала:" + atm.getNumberOfNominal());


        ArrayList<Integer> nominEx = new ArrayList<>(Arrays.asList(100, 500, 5000));
        ArrayList<Integer> nominNumberEx = new ArrayList<>(Arrays.asList(4, 6, 8));
        atm.depositeMoney(nominEx, nominNumberEx);
        System.out.println("Присутсвующий в банкомате номинал:" + atm.getNominal());
        System.out.println("Количество купюр каждого номинала:" + atm.getNumberOfNominal());
        System.out.println("Баланс средств в банкомате после внесения: " + atm.balance());
    }
}