package ru.otus.atmDepartment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class AtmDepartmentWork {
    public static void main(String[] args) {
        var atmDepartment = new AtmDepartment();
        AtmFacade atmFacade1 = new AtmFacade(new AtmProcessor(new ArrayList<>(Arrays.asList(100, 0, 30, 10, 5, 0, 20))));
        AtmFacade atmFacade2 = new AtmFacade(new AtmProcessor(new ArrayList<>(Arrays.asList(50, 2, 300, 10, 9, 7, 2))));
        AtmFacade atmFacade3 = new AtmFacade(new AtmProcessor(new ArrayList<>(Arrays.asList(400, 5, 10, 100, 5, 8, 0))));
        AtmFacade atmFacade4 = new AtmFacade(new AtmProcessor(new ArrayList<>(Arrays.asList(30, 7, 3, 0, 60, 0, 1))));
        atmDepartment.addObserver(atmFacade1);
        atmDepartment.addObserver(atmFacade2);
        atmDepartment.addObserver(atmFacade3);
        atmDepartment.addObserver(atmFacade4);
        System.out.println("Баланс средств во всех банкоматах объединения: "+ atmDepartment.balanceOfAllAtms());
        atmFacade1.getAtmProcessor().withDrawMoney(100000);
        atmFacade2.getAtmProcessor().withDrawMoney(100000);
        System.out.println("Баланс средств во всех банкоматах объединения: "+ atmDepartment.balanceOfAllAtms());
        atmDepartment.updateObservers();
        System.out.println("Баланс средств во всех банкоматах объединения: "+ atmDepartment.balanceOfAllAtms());
        atmDepartment.removeObserver(atmFacade4);
        System.out.println("Баланс средств во всех банкоматах объединения: "+ atmDepartment.balanceOfAllAtms());
        atmFacade1.getAtmProcessor().withDrawMoney(100000);
        System.out.println("Баланс средств во всех банкоматах объединения: "+ atmDepartment.balanceOfAllAtms());
        atmDepartment.updateObservers();
        System.out.println("Баланс средств во всех банкоматах объединения: "+ atmDepartment.balanceOfAllAtms());

   }
}


