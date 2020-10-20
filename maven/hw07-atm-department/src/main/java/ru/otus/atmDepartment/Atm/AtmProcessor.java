package ru.otus.atmDepartment.Atm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.TreeMap;

/**
 * класс реализует инициализцаию атм с кассетами различных номиналов и функционал его работы
 **/
public class AtmProcessor implements Atm, Cloneable {

    private final TreeMap<Integer, Integer> casseteMap = new TreeMap<>(Collections.reverseOrder());

    public AtmProcessor(ArrayList<Integer> numberOfEachNominal) {
        if (NominalForCassette.values().length != numberOfEachNominal.size())
            throw new RuntimeException("Несоответсвие количества наборов купюр количеству номинала.");
        else {
            int i = 0;
            for (var nominal : NominalForCassette.values()) {
                casseteMap.put(nominal.getValueOfNominal(), numberOfEachNominal.get(i++));
            }
        }
    }

    private AtmProcessor(AtmProcessor atmProcessor) {
        casseteMap.clear();
        casseteMap.putAll(atmProcessor.getCassetteMap());
    }

    @Override
    public AtmProcessor clone() {
        return new AtmProcessor(this);
    }

    TreeMap<Integer, Integer> getCassetteMap() {
        return casseteMap;
    }

    /**
     * внесение средств в атм
     **/
    @Override
    public void depositeMoney(ArrayList<Integer> nominal, ArrayList<Integer> numberOfNominal) {
        var depositeToCassette = new DepositeToCassettes(nominal, numberOfNominal);
        depositeToCassette.execute(this);
    }

    /**
     * снятие средств из атм
     **/
    @Override
    public void withDrawMoney(int sum) {
        var withDrawToCassette = new WithDrawToATM(sum);
        withDrawToCassette.execute(this);
        System.out.println(withDrawToCassette.getDetailsOfLastWithdrawing());
    }

    /**
     * Возможный номинал в банкомате
     **/
    Collection<Integer> getNumberOfNominal() {
        return casseteMap.values();
    }

    /**
     * Количество купюр каждого номинала в банкомате
     **/
    Collection<Integer> getNominal() {
        return casseteMap.keySet();
    }


    /**
     * общая сумма денежных средств на кассете
     **/
    @Override
    public double balance() {
        var cassetteBalance = new ATMBalance();
        cassetteBalance.execute(this);
        return cassetteBalance.getBalance();
    }
}
