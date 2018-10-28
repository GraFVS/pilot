package fields;

import com.codeborne.selenide.SelenideElement;

public class Input extends AbstractField{

    public Input(SelenideElement selenideElement, String name) {
        super(selenideElement, name);
    }

    @Override
    public void clear() {
        selenideElement.clear();
    }

    @Override
    public String toString() {
        return "Input{" +
                "name='" + name + '\'' +
                '}';
    }
}
