package ru.otus.atmDepartment;

/**Класс реализует подсчет суммы объединения банкоматов на всех банкоматах **/
public class AllAtmBalance implements Command{
    private double sumOfAllAtm ;
    private AtmDepartment atmDepartment;

    AllAtmBalance(AtmDepartment atmDepartment){
        this.atmDepartment = atmDepartment;
    }

    @Override
    public void execute(){
        sumOfAllAtm=0;
        for (AtmFacade a: atmDepartment.getListOfAtmFacade()){
              sumOfAllAtm += a.getBalance();
         }
    }

    double getBalance(){
        return sumOfAllAtm;
    }

}
