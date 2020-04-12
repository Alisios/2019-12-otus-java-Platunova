package ru.otus.Instrumentation;

public class ProxyCallingClass {
    public static void main(String[] args) {
        TestLoggingInterface testLogging = IoC.createLoggingProxy(new TestLogging(),TestLoggingInterface.class, TestLoggingAnnotation.class);
        testLogging.calculation(127);
        testLogging.calculation(4, 5 );
    }
}


