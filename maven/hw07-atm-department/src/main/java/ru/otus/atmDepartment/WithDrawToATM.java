package ru.otus.atmDepartment;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

/** класс реализует снятие денежных средств из атм с кассет различных номиналов **/
class WithDrawToATM implements Command{
    private AtmProcessor atmProcessor;
    private int sum;
    private TreeMap<Integer, Integer> out = new TreeMap<>(Collections.reverseOrder());

    WithDrawToATM(AtmProcessor atmProcessor, int sum){
        this.atmProcessor=atmProcessor;
        this.sum=sum;
    }

    @Override
    public void execute(){
        if (sum > atmProcessor.balance())
            throw new RuntimeException("Запрошенная сумма некорректна. Попробуйте задать меньшую сумму.");
        if ((sum % (Collections.min(atmProcessor.getNominal())))!=0)
            throw new RuntimeException("Запрошенная сумма некорректна.");
        int sumTemp = sum;

        for (Map.Entry<Integer, Integer> i: atmProcessor.getCassetteMap().entrySet()) {
            int  nominal = i.getKey();
            int  numberOfNominal = i.getValue();
            if (sumTemp > 0) {
                int temp = nominal * numberOfNominal;
                if (temp < sumTemp) {
                    out.put(nominal, numberOfNominal);
                    atmProcessor.getCassetteMap().put(nominal, 0);
                    sumTemp -= temp;
                }
                else {
                    if (sumTemp >= nominal) {
                        int nom = (int) (sumTemp / nominal);
                        atmProcessor.getCassetteMap().put(nominal, numberOfNominal - nom);
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

