package fields;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementsCollectionWrapper;
import enviroment.Init;
import org.openqa.selenium.By;
import org.openqa.selenium.NotFoundException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.FluentWait;
import utils.ActionWait;

import java.util.List;
import java.util.concurrent.TimeUnit;

public abstract class AbstractField {

    protected final SelenideElement selenideElement;
    protected final String name;
    final static int VERY_SHORT_TIMEOUT = 5;

    public AbstractField(SelenideElement selenideElement, String name) {
        this.selenideElement = selenideElement;
        this.name = name;
    }

    public SelenideElement getSelenideElement() {
        return selenideElement;
    }

    public boolean isEnabled() {
        return selenideElement.is(Condition.enabled);
    }

    public void click() {
        selenideElement.shouldBe(Condition.enabled).shouldBe(Condition.visible).click();
    }

    public void fillUp(String s) {
        throw new IllegalStateException("Данный тип поля не подразумевает заполнение. Метод должен быть переопределён.");
    }

    public ElementsCollection getOptions() {
        throw new IllegalStateException("Метод должен быть переопределён.");
    }

    public String getCharCounterValue() {
        throw new IllegalStateException("Метод должен быть переопределён.");
    }

    public void clear() {
        throw new IllegalStateException("Данный тип поля не подразумевает очистку. Метод должен быть переопределён.");
    }

    public String getText() {
        return selenideElement.shouldBe(Condition.enabled).shouldBe(Condition.visible).getText();
    }

    public String getValue() {
        isDisplayed();
        return selenideElement.getValue();
    }

    public String getAttribute(String attribute) {
        return selenideElement.getAttribute(attribute);
    }

    public WebElement getWrappedElement() {
        return selenideElement.getWrappedElement();
    }

    public void validate() {
        throw new IllegalStateException("Валидация данного поля не доступна");
    }

    public boolean isActive() {
        return false;
    }

    public boolean isDisplayed() {
        return isDisplayed(true);
    }

    //Метод по прежнему возвращает ответ о видимости, но в зависимости от того, что мы ожидаем, инвертируется ожидание
    public boolean isDisplayed(boolean expectedCondition) {
        try {
            if (new FluentWait<>(Init.getWebDriver())
                    .withTimeout(VERY_SHORT_TIMEOUT, TimeUnit.SECONDS)
                    .pollingEvery(1, TimeUnit.SECONDS)
                    .until(webDriver -> selenideElement.isDisplayed() == expectedCondition))
                return expectedCondition;
        } catch (TimeoutException ignoring) {
        }
        return !expectedCondition;
    }

    public String getCssValue(String value) {
        return selenideElement.getCssValue(value);
    }


    public void hover() {
        selenideElement.shouldBe(Condition.enabled).shouldBe(Condition.visible).hover();
    }

    protected ElementsCollection getChildElements(By by) {
        List<WebElement> webElements = ActionWait.withTimeout(5).pollingEvery(200, TimeUnit.MILLISECONDS)
                .ignoring(NotFoundException.class).call(
                        webdriver -> {
                            List<WebElement> elements = selenideElement.getWrappedDriver().findElements(by);
                            if (elements.size() > 0) return elements;
                            else {
                                selenideElement.click();
                                throw new NotFoundException("В списке выбора отсутсвуют элементы для выбора");
                            }
                        });
        return new ElementsCollection(new WebElementsCollectionWrapper(webElements));
    }

    abstract public String toString();
}
