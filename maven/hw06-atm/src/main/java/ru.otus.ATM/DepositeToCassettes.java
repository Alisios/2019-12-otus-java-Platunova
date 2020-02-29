package ru.otus.ATM;
import java.util.ArrayList;

/** класс реализует внесение денежных средств в атм по кассетам **/
class DepositeToCassettes implements Command{
    private AtmProcessor atmProcessor;
    private ArrayList<Integer> nominal = new ArrayList<>();
    private ArrayList<Integer> numberOfNominal=new ArrayList<>();
    DepositeToCassettes(AtmProcessor atmProcessor, ArrayList<Integer> nominal, ArrayList<Integer> numberOfNominal){
        this.atmProcessor = atmProcessor;
        this.nominal.addAll(nominal);
        this.numberOfNominal.addAll(numberOfNominal);
    }

    @Override
    public void execute() {
        for (int i = 0; i < nominal.size(); i++) {
            if (atmProcessor.getCassetteMap().containsKey(nominal.get(i))) {
                atmProcessor.getCassetteMap().put(nominal.get(i), atmProcessor.getCassetteMap().get(nominal.get(i)) + numberOfNominal.get(i));
            } else
                throw new RuntimeException("Неизвестный номинал при внесении.");
        }
    }
}

