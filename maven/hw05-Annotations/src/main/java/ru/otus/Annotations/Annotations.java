package ru.otus.Annotations;


public class Annotations {
    public static void main(String[] args) throws Exception {
       for (int i=0; i<100; i++) {
           TestLaunch.launch("ContainerTest");
           System.out.println("-------------------------");
       }
       TestLaunch.Result();
    }
}
