package ru.otus.ATM;

import java.util.Map;

/** класс реализует расчет и выдачу суммы денежных средств на кассете **/
class CassetteBalance{

    private double sum = 0;
    private Cassette cassette;
    CassetteBalance(Cassette cassette){
       this.cassette = cassette;
    }

    void execute(){
        for (Map.Entry<Integer, Integer> i: cassette.getCassette().entrySet())
            sum += i.getKey()*i.getValue();
    }

    double getBalance(){
        return sum;
    }

}