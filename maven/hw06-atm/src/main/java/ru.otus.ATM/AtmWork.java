package ru.otus.ATM;

public class AtmWork {
    public static void main(String[] args) {
        int [] initialStateOfNominal = {100, 0,  30, 10, 5, 0, 2};
        var atm = new Cassette(initialStateOfNominal);
        System.out.println("Присутсвующий в банкомате номинал:" + atm.getNominal());
        System.out.println("Количество купюр каждого номинала:" + atm.getNumberOfNominal());
        int[] nominEx ={100,500,5000};
        int[] nominNumberEx ={4, 6, 8};
        System.out.println("Баланс средств в банкомате: " + atm.balance());
        atm.withDrawMoney(17250);
        System.out.println("Баланс средств в банкомате после снятия: "+ atm.balance());
        atm.depositeMoney(nominEx,nominNumberEx);
        System.out.println("Баланс средств в банкомате после внесения: "+ atm.balance());
        System.out.println("Присутсвующий в банкомате номинал:" + atm.getNominal());
        System.out.println("Количество купюр каждого номинала:" + atm.getNumberOfNominal());
    }
}
