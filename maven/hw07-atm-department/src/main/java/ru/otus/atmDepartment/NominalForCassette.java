package ru.otus.atmDepartment;

enum NominalForCassette {

    FIFTY (50),
    HUNDRED (100),
    TWO_HUNDRED (200),
    FIFTY_HUNDRED (500),
    THOUSAND (1000),
    TWO_THOUSAND (2000),
    FIVE_THOUSAND (5000);

    private int value;

    NominalForCassette(int value) {
        this.value = value;
    }

    int getValueOfNominal() {
        return value;
    }

}