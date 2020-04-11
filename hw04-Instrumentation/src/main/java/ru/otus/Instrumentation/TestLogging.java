package ru.otus.Instrumentation;

class TestLogging implements TestLoggingInterface {
    @Override
    @TestLoggingAnnotation
    public void calculation(int param) {
        System.out.println("Some calculations with "+ param);
    }

    @Override
    public void calculation(int param, int param2) {
        System.out.println("Some calculations with "+ param + " " + param2);
    }
}
