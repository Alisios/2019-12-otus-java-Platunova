package ru.otus.ATM;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

/** класс реализует снятие денежных средств с кассеты **/
public class WithDrawToCassette {
    private Cassette cassette;
    private TreeMap<Integer, Integer> out = new TreeMap<>(Collections.reverseOrder());

    WithDrawToCassette(Cassette cassette){
        this.cassette=cassette;
    }

  void execute (int sum){
        if (sum > cassette.balance())
            throw new RuntimeException("Запрошенная сумма некорректна. Попробуйте задать меньшую сумму.");
        if ((sum % (Collections.min(cassette.getNominal())))!=0)
            throw new RuntimeException("Запрошенная сумма некорректна.");
        int sumTemp = sum;

        for (Map.Entry<Integer, Integer> i: cassette.getCassette().entrySet()) {
            int  nominal = i.getKey();
            int  numberOfNominal = i.getValue();
            if (sumTemp > 0) {
                int temp = nominal * numberOfNominal;
                if (temp < sumTemp) {
                    out.put(nominal, numberOfNominal);
                    cassette.getCassette().put(nominal, 0);
                    sumTemp -= temp;
                }
                else {
                    if (sumTemp >= nominal) {
                        int nom = (int) (sumTemp / nominal);
                        cassette.getCassette().put(nominal, numberOfNominal - nom);
                        out.put(nominal, nom);
                        sumTemp -= nom*nominal;
                    }
                }
            }
        }
    }

    String getDetailsOfLastWithdrawing(){
        return "Номинал для выдачи: " + out.keySet() + ". Количество купюр для выдачи: " + out.values();
    }

}
