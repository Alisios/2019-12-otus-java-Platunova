package ru.otus.atmDepartment;
import java.util.ArrayList;


/**Сохранение состояния атм и его сброс до начального состояния**/
class Originator {
        private final ArrayList <Memento> arrayList = new ArrayList<>();

        void saveState(AtmProcessor state) {
            arrayList.add(new Memento(state));
        }

    AtmProcessor restoreState() {
            return arrayList.get(0).getState();
        }
    }

