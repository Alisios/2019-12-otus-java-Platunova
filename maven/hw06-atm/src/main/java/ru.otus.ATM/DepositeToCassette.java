package ru.otus.ATM;

/** класс реализует внесение денежных средств на кассету **/
class DepositeToCassette {
    private Cassette cassette;
    DepositeToCassette(Cassette cassette){
        this.cassette = cassette;
    }
    void execute(int[] nominal, int[] numberOfNominal) {
        for (int i = 0; i < nominal.length; i++) {
            if (cassette.getCassette().containsKey(nominal[i])) {
                cassette.getCassette().put(nominal[i], cassette.getCassette().get(nominal[i]) + numberOfNominal[i]);
            } else
                throw new RuntimeException("Неизвестный номинал.");
        }
    }
}
