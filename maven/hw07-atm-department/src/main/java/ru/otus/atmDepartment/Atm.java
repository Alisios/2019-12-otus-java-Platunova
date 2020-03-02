package ru.otus.atmDepartment;
import java.util.ArrayList;

/**интерфейс для работы с банкоматом**/
/**можно работать с клиентом и кассетой**/
interface Atm {
    void depositeMoney(ArrayList<Integer> nominal, ArrayList<Integer> numberOfNominal) ;
    void withDrawMoney(int sum);
}
