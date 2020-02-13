package ru.otus.Annotations;
import java.lang.reflect.Method;
import java.util.ArrayList;

class TestLaunch {
    private static long exceptionsForAllTests = 0;
    private static long numberOfSuccessfulTests = 0;
    static void launch(String name) throws Exception {
        Class obj = Class.forName(name);
        Method[] methodsOfObject = obj.getDeclaredMethods();
        ArrayList<Method> BeforeList = new ArrayList<>();
        ArrayList<Method> AfterList = new ArrayList<>();
        ArrayList<Method> TestList = new ArrayList<>();

        for (var m1 : methodsOfObject) {
            if (m1.isAnnotationPresent(Before.class))
                BeforeList.add(m1);
            if (m1.isAnnotationPresent(Test.class))
                TestList.add(m1);
            if (m1.isAnnotationPresent(After.class))
                AfterList.add(m1);
        }
        for (Method mTest : TestList) {
            Object testObj = obj.newInstance();
            for (Method mBefore : BeforeList) {
                mBefore.setAccessible(true);
                mBefore.invoke(testObj);
            }
            try {
                mTest.setAccessible(true);
                mTest.invoke(testObj);
                numberOfSuccessfulTests++;
            }
            catch (Exception e) {
                e.printStackTrace();
                exceptionsForAllTests++;
            }
            for (Method mAfter : AfterList) {
                mAfter.setAccessible(true);
                mAfter.invoke(testObj);
            }
        }
    }
    static void ResultTrace(){
        System.out.println("There was "+ (numberOfSuccessfulTests+ exceptionsForAllTests) + " tests: "+
                numberOfSuccessfulTests + " successfull test(s) and "+ exceptionsForAllTests + " failed test(s).");
    }
}
