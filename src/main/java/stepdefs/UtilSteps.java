package stepdefs;

import cucumber.api.DataTable;
import cucumber.api.java.After;
import cucumber.api.java.ru.Дано;
import cucumber.api.java.ru.И;
import cucumber.api.java.ru.Тогда;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

import java.time.temporal.TemporalUnit;

import static enviroment.Init.closeDriver;
import static enviroment.Init.driverStarted;
import static enviroment.Init.initDriver;
import static java.time.temporal.ChronoUnit.SECONDS;
import static utils.Evaluator.getVariable;
import static utils.Evaluator.setVariable;


public class UtilSteps {

    private static final Logger LOG = LoggerFactory.getLogger(UtilSteps.class);

    public static void freeze(long amountOfTime, TemporalUnit timeUnit) {
        Duration duration = Duration.of(amountOfTime, timeUnit);
        try {
            Thread.sleep(duration.toMillis());
        } catch (InterruptedException e) {
            LOG.info(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Тогда("приостановлено выполнение на (\\d+) секунд")
    public void stopExecution(long amountOfTimeInSeconds) {
        freeze(amountOfTimeInSeconds, SECONDS);
    }


    @Дано("^текущий пользователь \"([^\"]*)\"$")
    public void setCurrentUser(String user) {
        setVariable("currentUser", user);
    }

    @Дано("^в переменной \"([^\"]*)\" сохраняется значение \"(.*)\"$")
    public void initVariable(String variable, String param) {
        String value = getVariable(param);
        setVariable(variable, value);
        LOG.info("В переменной {} сохранено значение {} ", variable, value);
    }

    @Дано("^в переменных сохраняются значения:$")
    public void initVariables(DataTable dataTable) {
        dataTable.asLists(String.class).forEach(row -> initVariable(row.get(0), row.get(1)));
    }



    @И("^выполняется рестарт вебдрайвера$")
    public void restartDriver() {
        closeDriver();
        initDriver();
    }

    @After(timeout = 15000)//({"завершение текущей сессии"})
    public void afterEachScenario() {
        if (driverStarted()) {
            closeDriver();
        }
    }
}