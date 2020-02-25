package ru.otus.Annotations;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import ru.otus.AnnotationsPackage.*;

class TestLaunch {
    private static long exceptionsForAllTests = 0;
    private static long numberOfSuccessfulTests = 0;
    static void launch(String name) throws Exception {
        Class obj = Class.forName(name);
        Method[] methodsOfObject = obj.getDeclaredMethods();
        ArrayList<Method> beforeList = new ArrayList<>();
        ArrayList<Method> afterList = new ArrayList<>();
        ArrayList<Method> testList = new ArrayList<>();

        for (var m1 : methodsOfObject) {
            if (m1.isAnnotationPresent(Before.class))
                beforeList.add(m1);
            if (m1.isAnnotationPresent(Test.class))
                testList.add(m1);
            if (m1.isAnnotationPresent(After.class))
                afterList.add(m1);
        }
        for (Method mTest : testList) {
            Constructor constructor = obj.getDeclaredConstructor();
            Object testObj= constructor.newInstance();
            for (Method mBefore : beforeList) {
                mBefore.setAccessible(true);
                mBefore.invoke(testObj);
            }
            try {
                mTest.setAccessible(true);
                mTest.invoke(testObj);
                numberOfSuccessfulTests++;
            }
            catch (Exception e) {
                System.out.println(e.getCause()+" in " + mTest.getName());
                exceptionsForAllTests++;
            }
            for (Method mAfter : afterList) {
                mAfter.setAccessible(true);
                mAfter.invoke(testObj);
            }
        }
    }
    static long getNumberOfSuccessfulTests(){
        return numberOfSuccessfulTests;
    }
    static long getNumberOfTests(){
        return numberOfSuccessfulTests+exceptionsForAllTests;
    }
    static long getNumberOfFailedTests(){
        return exceptionsForAllTests;
    }
}
