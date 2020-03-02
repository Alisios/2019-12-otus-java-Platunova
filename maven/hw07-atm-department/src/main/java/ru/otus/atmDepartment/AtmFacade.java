package ru.otus.atmDepartment;

/**Класс предоставляет собой упрощенный функционал банкомата AtmProcessor**/
public class AtmFacade implements Observer{
    private AtmProcessor atmProcessor;
    private Originator originator = new Originator();

    AtmFacade(AtmProcessor atmProcessor){
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

    AtmProcessor getAtmProcessor(){
        return atmProcessor;
    }
}
