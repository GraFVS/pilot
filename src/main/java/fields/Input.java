package fields;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import utils.ActionWait;

import java.util.concurrent.TimeUnit;

public class Input extends AbstractField{

    public Input(SelenideElement selenideElement, String name) {
        super(selenideElement, name);
    }

    @Override
    public void clear() {
        selenideElement.shouldBe(Condition.enabled).shouldBe(Condition.visible).clear();
    }

    @Override
    public String toString() {
        return String.format("Input{name='%s'}", name);
    }

    @Override
    public void fillUp(String s) {
        ActionWait.shortTimeout().pollingEvery(1, TimeUnit.SECONDS).safeCall(e -> {
            clear();
            selenideElement.sendKeys(s);
            return selenideElement.getValue().equals(s);
        });
    }
}
