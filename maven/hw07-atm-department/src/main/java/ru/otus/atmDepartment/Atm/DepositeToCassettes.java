package ru.otus.atmDepartment.Atm;

import java.util.ArrayList;

/**
 * класс реализует внесение денежных средств в атм по кассетам
 **/
class DepositeToCassettes implements Command {
    private final ArrayList<Integer> nominal = new ArrayList<>();
    private final ArrayList<Integer> numberOfNominal = new ArrayList<>();

    DepositeToCassettes(ArrayList<Integer> nominal, ArrayList<Integer> numberOfNominal) {
        this.nominal.addAll(nominal);
        this.numberOfNominal.addAll(numberOfNominal);
    }

    @Override
    public void execute(AtmProcessor atmProcessor) {
        for (int i = 0; i < nominal.size(); i++) {
            if (atmProcessor.getCassetteMap().containsKey(nominal.get(i))) {
                atmProcessor.getCassetteMap().put(nominal.get(i), atmProcessor.getCassetteMap().get(nominal.get(i)) + numberOfNominal.get(i));
            } else
                throw new RuntimeException("Неизвестный номинал при внесении.");
        }
    }
}

