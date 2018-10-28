package fields;

import com.codeborne.selenide.SelenideElement;

public class Button extends AbstractField{

    public Button(SelenideElement selenideElement, String name) {
        super(selenideElement, name);
    }


    @Override
    public String toString() {
        return String.format("Button{name='%s'}", name);
    }
}