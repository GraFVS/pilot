import org.junit.AfterClass;
import runner.CucumberRunner;

public class CucumberRunnerTest extends CucumberRunner {

    @AfterClass
    public static void tearDown(){
        System.out.println("Завершение кукумбертестов");
    }

}
