package ru.otus.Annotations;

public class Annotations {
    public static void main(String[] args) throws Exception {
        TestLaunch.launch("ru.otus.Annotations.ContainerTest");
        System.out.println("There was "+ TestLaunch.getNumberOfTests() + " tests: " +
         TestLaunch.getNumberOfSuccessfulTests() + " successfull test(s) and "+
                TestLaunch.getNumberOfFailedTests() + " failed test(s).");
    }
}
