package ru.otus.atmDepartment.Atm;

import java.util.Map;

/**
 * класс реализует расчет и выдачу суммы денежных средств в атм
 **/
class ATMBalance implements Command {

    private double sum = 0;

    @Override
    public void execute(AtmProcessor atmProcessor) {
        for (Map.Entry<Integer, Integer> i : atmProcessor.getCassetteMap().entrySet())
            sum += i.getKey() * i.getValue();
    }

    double getBalance() {
        return sum;
    }

}