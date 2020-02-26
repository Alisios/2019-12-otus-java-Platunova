package ru.otus.ATM;
import java.util.Collection;
import java.util.Collections;
import java.util.TreeMap;

/** инициализация кассеты**/
class Cassette implements Atm {
    private TreeMap<Integer, Integer> casseteMap =  new TreeMap<>(Collections.reverseOrder());

    Cassette(int[] numberOfEachNominal) {
        for (int i = 0; i < numberOfEachNominal.length; i++) {
            Integer[] NOMINAL = new Integer[]{50, 100, 200, 500, 1000, 2000, 5000};
            casseteMap.put(NOMINAL[i], numberOfEachNominal[i]);
        }
    }

    TreeMap<Integer, Integer> getCassette(){
        return casseteMap;
    }

    /** внесение средств на кассету **/
    @Override
    public void depositeMoney(int [] nominal, int[] numberOfNominal){
        var depositeToCassette = new DepositeToCassette(this);
        depositeToCassette.execute(nominal, numberOfNominal);
    }

    /** снятие средств с кассеты **/
    @Override
   public void withDrawMoney(int sum){
        var withDrawToCassette = new WithDrawToCassette(this);
        withDrawToCassette.execute(sum);
        System.out.println(withDrawToCassette.getDetailsOfLastWithdrawing());
    }

    /** общая сумма денежных средств на кассете **/
    @Override
    public double balance(){
        var cassetteBalance = new CassetteBalance(this);
        cassetteBalance.execute();
        return cassetteBalance.getBalance();
    }

    /**Возможный номинал в банкомате**/
    Collection<Integer> getNumberOfNominal(){
        return casseteMap.values();
    }

    /**Количество купюр каждого номинала в банкомате**/
    Collection<Integer> getNominal(){
        return casseteMap.keySet();
    }

}

