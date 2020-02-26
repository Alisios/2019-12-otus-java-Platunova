package ru.otus.ATM;

/**интерфейс для работы с банкоматом**/
/**можно работать с клиентом и кассетой**/
interface Atm {
    void depositeMoney(int [] nominal, int[] numberOfNominal) ;
    void withDrawMoney(int sum);
    double balance();
}
