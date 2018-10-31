package utils;

import enviroment.Init;
import org.openqa.selenium.NotFoundException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.FluentWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import static enviroment.Stand.getCurrentStand;

public class ActionWait extends FluentWait<WebDriver> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ActionWait.class);

    private static final int SHORT_TIMEOUT = Integer.parseInt(getCurrentStand().getConfig().getString("action.wait.timeouts.short"));
    private static final int LONG_TIMEOUT = Integer.parseInt(getCurrentStand().getConfig().getString("action.wait.timeouts.long"));
    private static final int DEFAULT_INTERVAL = Integer.parseInt(getCurrentStand().getConfig().getString("action.wait.polling.interval"));

    private ActionWait(WebDriver driver) {
        super(driver);
    }

    public static ActionWait build() {
        return new ActionWait(Init.getWebDriver());
    }

    public static ActionWait shortTimeout() {
        return withTimeout(SHORT_TIMEOUT);
    }

    public static ActionWait longTimeout() {
        return withTimeout(LONG_TIMEOUT);
    }

    public static ActionWait withTimeout(long timeoutInSeconds) {
        return (ActionWait) build().withTimeout(timeoutInSeconds, TimeUnit.SECONDS)
                .pollingEvery(DEFAULT_INTERVAL, TimeUnit.MILLISECONDS)
                .ignoring(StaleElementReferenceException.class)
                .ignoring(NotFoundException.class);
    }

    @Override
    public ActionWait pollingEvery(long duration, TimeUnit unit) {
        return (ActionWait) super.pollingEvery(duration, unit);
    }

    @Override
    public ActionWait ignoring(Class<? extends Throwable> exceptionType) {
        return (ActionWait) super.ignoring(exceptionType);
    }

    /**
     * Метод обертка для until, создаёт новый инстанс ExpectedCondition от нашей входной функции
     *
     * @param function
     * @param <T>
     * @return
     */
    public <T> T call(Function<WebDriver, T> function) {
        return until(new ExpectedCondition<T>() {
            @Nullable
            @Override
            public T apply(@Nullable WebDriver driver) {
                return function.apply(driver);
            }
        });
    }

    /**
     * Вызывает функцию, если время вышло возвращает false
     *
     * @param function
     * @return result у функции, если время вышло, то - false
     */
    public Boolean safeCall(Function<WebDriver, Boolean> function) {
        try {
            return call(function);
        } catch (TimeoutException ignore) {
            LOGGER.debug("Время ожидания истекло. " + ignore);
            return false;
        }
    }
}
