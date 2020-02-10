package ru.otus.Annotations;
import java.lang.reflect.Method;

class TestLaunch {
    private static long exceprionsForAllTests = 0;
    private static long numberOfTests = 0;
    private static long counter = 0;
    static void launch(String name) throws Exception {
        String className = "ru.otus.Annotations" + "." + name;
        Class obj = Class.forName(className);
        Object testObj = obj.newInstance();
        try {
            Method[] m = obj.getDeclaredMethods();
            for (var m1 : m)
                if (m1.isAnnotationPresent(Before.class)) {
                    m1.setAccessible(true);
                    m1.invoke(testObj);
                } else if (m1.isAnnotationPresent(Test.class)) {
                    m1.setAccessible(true);
                    m1.invoke(testObj);
                } else if (m1.isAnnotationPresent(After.class)) {
                    m1.setAccessible(true);
                    m1.invoke(testObj);
                }
        }
        catch (Exception e) {
            e.printStackTrace();
            counter++;
        }
        finally {
            Method meth_error = testObj.getClass().getMethod("getErrors");
            meth_error.setAccessible(true);
            exceprionsForAllTests +=(long) meth_error.invoke(testObj);;

            Method meth_number = testObj.getClass().getMethod("getTestNumber");
            meth_number.setAccessible(true);
            numberOfTests +=(long) meth_number.invoke(testObj);
        }
    }
    static void Result(){
        System.out.println("There were "+ numberOfTests + "  tests. Among them "+
                (numberOfTests-exceprionsForAllTests) + " successfull tests and "+ exceprionsForAllTests + " failed tests.");
        System.out.println("Number of exceptions in launch "+ counter );
    }

}
