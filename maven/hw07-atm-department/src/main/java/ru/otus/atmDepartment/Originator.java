package ru.otus.atmDepartment;

import ru.otus.atmDepartment.Atm.Atm;

/**
 * Сохранение состояния атм и его сброс до начального состояния
 **/
class Originator {
    private Memento memento;

    void saveState(Atm state) {
        memento = new Memento(state);

    }

    Atm restoreState() {
        return memento.getState();
    }
}

