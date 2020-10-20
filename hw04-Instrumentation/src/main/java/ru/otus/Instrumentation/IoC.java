package ru.otus.Instrumentation;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class IoC<T> {

    @SuppressWarnings("unchecked")
    static <T> T createLoggingProxy(T testLogging, Class<?> testLoggingInterface, Class<? extends Annotation> testLoggingInterfaceAnnotation) {
        InvocationHandler handler = new LogInvocationHandler<>(testLogging, testLoggingInterfaceAnnotation);

        return (T) Proxy.newProxyInstance(IoC.class.getClassLoader(),
                new Class<?>[]{testLoggingInterface}, handler);
    }

    static class LogInvocationHandler<T> implements InvocationHandler {
        private final T testLogging;
        private final List<String> methodsWithAnnotationList = new ArrayList<>();

        LogInvocationHandler(T testLogging, Class<? extends Annotation> testLoggingInterfaceAnnotation) {
            this.testLogging = testLogging;
            defineMethodsWithAnnotation(testLogging, testLoggingInterfaceAnnotation);
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (ifLogging(method)) {
                System.out.println("executed method : " + method.getName() + ", params: " + Arrays.toString(args));
            }
            return method.invoke(testLogging, args);
        }

        private boolean ifLogging(Method method) {
            return methodsWithAnnotationList.contains(method.getName() + Arrays.toString(method.getParameterTypes()) + method.getReturnType().getName());
        }

        private void defineMethodsWithAnnotation(T testLogging, Class<? extends Annotation> testLoggingInterfaceAnnotation) {
            Class<?> cl = testLogging.getClass();
            for (Method m : cl.getDeclaredMethods()) {
                if (m.isAnnotationPresent(testLoggingInterfaceAnnotation)) {
                    methodsWithAnnotationList.add(m.getName() +
                            Arrays.toString(m.getParameterTypes()) +
                            m.getReturnType().getName());
                }
            }
        }

    }
}
