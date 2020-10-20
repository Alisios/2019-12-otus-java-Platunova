package ru.otus.atmDepartment;


/**
 * Каждый потенциальный наблюдатель должен реализовывать заданный интерфейс с единственным методом,
 * который сбрасывает состояние ситсемы до начального
 **/
interface Observer {
    void restoreInitialState();
}
