package ru.otus.ATM;

import java.util.Map;

/** класс реализует расчет и выдачу суммы денежных средств в атм **/
class ATMBalance implements Command{

    private double sum = 0;
    private AtmProcessor atmProcessor;
    ATMBalance(AtmProcessor atmProcessor){
        this.atmProcessor = atmProcessor;
    }

    @Override
    public void execute(){
        for (Map.Entry<Integer, Integer> i: atmProcessor.getCassetteMap().entrySet())
            sum += i.getKey()*i.getValue();
    }

    double getBalance(){
        return sum;
    }

}