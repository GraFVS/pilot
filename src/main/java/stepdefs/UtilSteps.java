package stepdefs;

import cucumber.api.java.After;
import cucumber.api.java.ru.И;
import cucumber.api.java.ru.Тогда;
import io.qameta.allure.Step;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

import java.time.temporal.TemporalUnit;

import static enviroment.Init.closeDriver;
import static enviroment.Init.driverStarted;
import static enviroment.Init.initDriver;
import static java.time.temporal.ChronoUnit.SECONDS;




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
    @Step
    public void stopExecution(long amountOfTimeInSeconds) {
        freeze(amountOfTimeInSeconds, SECONDS);
    }



    @И("^выполняется рестарт вебдрайвера$")
    @Step
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