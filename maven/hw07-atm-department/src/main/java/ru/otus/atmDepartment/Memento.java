package ru.otus.atmDepartment;

import ru.otus.atmDepartment.Atm.Atm;

public class Memento {
    private final Atm state;

    Memento(Atm state) {
        this.state = state.clone();
    }

    Atm getState() {
        return state.clone();
    }
}
