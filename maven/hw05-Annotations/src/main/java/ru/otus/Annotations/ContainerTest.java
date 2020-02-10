package ru.otus.Annotations;

public class ContainerTest {
    private long ErrorCounter = 0;
    private long TestNumber = 0;

    @Before
    public void Settings()throws Exception  {
        try{
            System.out.println("Settings");
            int h = 100/0;
        }
        catch (Exception e) {
            e.printStackTrace();
            ErrorCounter++;
        }
        finally {
            TestNumber++;
        }
    };

    @Before
    public void Settings2() throws Exception {
        try{
        System.out.println("Settings2");
    }
        catch (Exception e) {
        e.printStackTrace();
        ErrorCounter++;
    }
        finally {
            TestNumber++;
        }
    };

    @Test
    public void Test1()throws Exception  {
        try{
        System.out.println("Test #1");
        int h = 100/0;
        }
        catch (Exception e) {
            e.printStackTrace();
            ErrorCounter++;
        }
        finally {
            TestNumber++;
        }
    };

    @Test
    public void Test2()throws Exception  {
        try{
            System.out.println("Test #2");
        }
        catch (Exception e) {
            e.printStackTrace();
            ErrorCounter++;
        }
        finally {
            TestNumber++;
        }
    };

    public void TestWithoutAnnotation() throws Exception {
        try{
            System.out.println("Test without Annotation");
        }
        catch (Exception e) {
            e.printStackTrace();
            ErrorCounter++;
        }
        finally {
            TestNumber++;
        }
    };

    @After
    public void CloseAll()throws Exception {
        try{
            System.out.println("Close all");
        }
        catch (Exception e) {
            e.printStackTrace();
            ErrorCounter++;
        }
        finally {
            TestNumber++;
        }
    };

    public long getErrors() { return ErrorCounter; }
    public long getTestNumber()
    {
        return TestNumber;
    }

}
