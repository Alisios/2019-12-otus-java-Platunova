package ru.otus.atmDepartment.Atm;

import java.util.ArrayList;

/**
 * интерфейс для работы с банкоматом
 **/

/**можно работать с клиентом и кассетой**/
public interface Atm extends Cloneable {
    void depositeMoney(ArrayList<Integer> nominal, ArrayList<Integer> numberOfNominal);

    void withDrawMoney(int sum);

    double balance();

    Atm clone();
}
