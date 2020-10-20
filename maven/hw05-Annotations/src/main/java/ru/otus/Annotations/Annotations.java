package ru.otus.Annotations;

public class Annotations {
    public static void main(String[] args) throws Exception {
        var testLaunch = new TestLaunch();
        testLaunch.launch("ru.otus.Annotations.ContainerTest");
        System.out.println("There was " + testLaunch.getNumberOfTests() + " tests: " +
                testLaunch.getNumberOfSuccessfulTests() + " successfull test(s) and " +
                testLaunch.getNumberOfFailedTests() + " failed test(s).");
    }
}
