package ru.otus.atmDepartment;


public class Memento {
    private final AtmProcessor state;

    Memento(AtmProcessor state) {
        this.state = state.clone();
    }

    AtmProcessor getState() {
        return state.clone();
    }
}
