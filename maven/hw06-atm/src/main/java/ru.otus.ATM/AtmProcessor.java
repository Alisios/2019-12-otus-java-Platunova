package ru.otus.ATM;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.TreeMap;

/**класс реализует инициализцаию атм с кассетами различных номиналов и функционал его работы **/
class AtmProcessor {

    private TreeMap<Integer, Integer> casseteMap =  new TreeMap<>(Collections.reverseOrder());

     AtmProcessor(ArrayList<Integer> numberOfEachNominal) {
        if (nominalsForCassette.values().length != numberOfEachNominal.size())
            throw new RuntimeException("Несоответсвие количества наборов купюр количеству номинала.");
        else {
            int i=0;
            for (var nominal : nominalsForCassette.values()) {
                casseteMap.put(nominal.getValueOfNominal(), numberOfEachNominal.get(i++));
            }
        }
    }

    TreeMap<Integer, Integer> getCassetteMap(){
        return casseteMap;
    }

    /** внесение средств в атм **/
    void depositeMoney(ArrayList<Integer> nominal, ArrayList<Integer> numberOfNominal){
        var depositeToCassette = new DepositeToCassettes(this, nominal, numberOfNominal);
        depositeToCassette.execute();
    }

    /** снятие средств из атм **/
    public void withDrawMoney(int sum){
        var withDrawToCassette = new WithDrawToATM(this, sum);
        withDrawToCassette.execute();
        System.out.println(withDrawToCassette.getDetailsOfLastWithdrawing());
    }


    /**Возможный номинал в банкомате**/
    Collection<Integer> getNumberOfNominal(){
        return casseteMap.values();
    }

    /**Количество купюр каждого номинала в банкомате**/
    Collection<Integer> getNominal(){
        return casseteMap.keySet();
    }


    /** общая сумма денежных средств на кассете **/
     double balance(){
        var cassetteBalance = new ATMBalance(this);
        cassetteBalance.execute();
        return cassetteBalance.getBalance();
    }
}
