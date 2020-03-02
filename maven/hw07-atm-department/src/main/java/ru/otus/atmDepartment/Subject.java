package ru.otus.atmDepartment;

/**Интерфейс субъекта. Используется объектами для регистрации в качестве наблюдателя, удаления из списка
 * наблюдателей и обновления всех наблюдателей сразу**/
public interface Subject {
     void addObserver(Observer o);
     void removeObserver(Observer o);
     void updateObservers();
}
