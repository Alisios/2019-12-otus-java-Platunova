package ru.otus.atmDepartment;


/**
 * Класс реализует подсчет суммы объединения банкоматов на всех банкоматах
 **/
class AllAtmBalance {
    private AtmDepartment atmDepartment;

    AllAtmBalance(AtmDepartment atmDepartment) {
        this.atmDepartment = atmDepartment;
    }

    double getBalance() {
        double sumOfAllAtm = 0;
        for (AtmFacade a : atmDepartment.getListOfAtmFacade()) {
            sumOfAllAtm += a.getBalance();
        }
        return sumOfAllAtm;
    }

}
