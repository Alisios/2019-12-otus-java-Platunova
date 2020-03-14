package ru.otus.ATM;

enum nominalsForCassette {

    FIFTY (50),
    HUNDRED (100),
    TWO_HUNDRED (200),
    FIFTY_HUNDRED (500),
    THOUSAND (1000),
    TWO_THOUSAND (2000),
    FIVE_THOUSAND (5000);

    private int value;

    nominalsForCassette(int value) {
        this.value = value;
    }

    int getValueOfNominal() {
        return value;
    }

}