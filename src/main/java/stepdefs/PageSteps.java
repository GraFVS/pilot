package stepdefs;

import com.codeborne.selenide.ex.UIAssertionError;
import cucumber.api.java.ru.Дано;
import cucumber.api.java.ru.И;
import enviroment.Init;
import exception.AutotestError;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.NotFoundException;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pages.AbstractPage;
import utils.ActionWait;

import java.util.concurrent.TimeUnit;

public class PageSteps {
    private static final Logger LOG = LoggerFactory.getLogger(PageSteps.class);

    @И("^пользователь переходит на страницу \"([^\"]*)\"$")
    public void goToLoginPage(String url) {
        WebDriver driver = Init.getWebDriver();
        LOG.debug("Загружается URL {}", url);
        driver.get(url);
        LOG.debug("Загружен URL {}", url);
    }

    @Дано("^открывается \"([^\"]*)\"$")
    public void checkPageIsOpened(String pageName) {
        AbstractPage page = Init.getPageFactory().getPage(pageName);
        boolean pageIsOpened = ActionWait.
                shortTimeout().
                ignoring(NoSuchElementException.class).
                ignoring(UIAssertionError.class).
                ignoring(NotFoundException.class).
                pollingEvery(500, TimeUnit.MILLISECONDS).
                safeCall(webDriver -> {
                    return page.isOpened();
                });
        if (!pageIsOpened)
            throw new AutotestError(String.format("Страница %s не открылась", pageName));
    }

}