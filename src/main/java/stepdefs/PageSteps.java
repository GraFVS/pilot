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
import utils.AccountCredentials;
import utils.ActionWait;

import java.util.concurrent.TimeUnit;

import static utils.Evaluator.getVariable;

public class PageSteps {
    private static final Logger LOG = LoggerFactory.getLogger(PageSteps.class);
    private static final FieldSteps FIELD_STEPS = new FieldSteps();


    @И("^пользователь переходит по URL: \"([^\"]*)\"$")
    public void goToLoginPage(String url) {
        WebDriver driver = Init.getWebDriver();
        LOG.debug("Загружается URL {}", url);
        driver.get(url);
        LOG.debug("Загружен URL {}", url);
    }

    @И("^пользователь переходит на страницу \"([^\"]*)\"$")
    public void gotoPage(String page) {
        WebDriver driver = Init.getWebDriver();
        LOG.debug("Выполняется попытка перехода на страницу {}", page);
        String url = Init.getPageFactory().getPageURL(page);
        if (!url.equals("")) {
            LOG.debug("Загружается URL {}", url);
            driver.get(url);
            LOG.debug("Загружен URL {}", url);
        } else {
            throw new AutotestError("На странице '" + page + "' в аннотации @PageEntry не указан URL");
        }
        //TODO: После добавления шагов по работе с полями на странице - встроить сюда проверку открытия страницы (checkPageIsOpened)
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

    @И("^пользователь авторизуется как \"([^\"]*)\"$")
    public void userAuthorized(String role) {
        AccountCredentials accountCredentials = new AccountCredentials(getVariable(role));
        FIELD_STEPS.fillUpField("Поле <Логин или номер телефона>", accountCredentials.getLogin());
        FIELD_STEPS.fillUpField("Поле <Пароль>", accountCredentials.getPassword());
        FIELD_STEPS.clickField("Кнопка <Войти>");
    }

}
