package ru.otus.atmDepartment;
import java.util.ArrayList;
import java.util.List;

/**Класс,реализующий объединение банкоматов, включающее в себя группу банкоматов. объединение может добалять новые банкоматы,
 * удалять их, сбрасывать состояние всех банкоматов до начального и вычислить сумму средств на всех банкоматах**/

class AtmDepartment implements ManagerOfObservers{
    private final List<Observer> atmList;

    AtmDepartment(){
        this.atmList = new ArrayList<>();
    }

    @Override
    public void addObserver(Observer observer){
        atmList.add(observer);
    }

    @Override
     public void removeObserver(Observer o){
        atmList.remove(o);
    }

    @Override
    public void updateObservers(){
        atmList.forEach(Observer::restoreInitialState);
    }

    double balanceOfAllAtms() {
        var atmBalance = new AllAtmBalance(this);
        return atmBalance.getBalance();
    }

    List<AtmFacade> getListOfAtmFacade(){
        List <AtmFacade> list = new ArrayList<>();
        for (Observer a: atmList)
            list.add((AtmFacade)a);
        return list;
    }
}