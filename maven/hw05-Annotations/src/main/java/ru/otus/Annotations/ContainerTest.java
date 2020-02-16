package ru.otus.Annotations;

public class ContainerTest  {
    @Before
    public void settings(){
        System.out.println("Settings");
    };

    @Before
    public void settings2()  {
        System.out.println("Settings2");
    };

    @Test
    public void test1() {
        System.out.println("Test #1");
    }

    @Test
    public void test2()  {
        System.out.println("Test #2");
    };

    public void testWithoutAnnotation()  {
        System.out.println("Test without Annotation");
    };

    @After
    public void closeAll() {
        System.out.println("Close all");
    };

    @Before
    public void settings3() {
        System.out.println("Settings3");
    }
}
