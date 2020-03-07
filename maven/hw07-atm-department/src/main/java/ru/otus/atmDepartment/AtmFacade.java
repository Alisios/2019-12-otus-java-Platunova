package ru.otus.atmDepartment;
import ru.otus.atmDepartment.Atm.Atm;

/**Класс предоставляет собой упрощенный функционал банкомата AtmProcessor**/
public class AtmFacade implements Observer{
    private Atm atmProcessor;
    private Originator originator = new Originator();

    AtmFacade(Atm atmProcessor){
        this.atmProcessor = atmProcessor;
        originator.saveState(this.atmProcessor);
    }

    @Override
    public void restoreInitialState() {
        this.atmProcessor =  originator.restoreState();
    }

    double getBalance(){
        return atmProcessor.balance();
    }

    Atm getAtmProcessor(){
        return atmProcessor;
    }
}
