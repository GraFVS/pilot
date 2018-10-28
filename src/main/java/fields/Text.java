package fields;

import com.codeborne.selenide.SelenideElement;

public class Text extends AbstractField{

    public Text(SelenideElement selenideElement, String name) {
        super(selenideElement, name);
    }

    @Override
    public void clear() {

    }

    @Override
    public String getText() {
        return selenideElement.innerText().replaceAll("\u00a0", " ").trim();
    }

    @Override
    public String getValue() {
        return getText();
    }

    @Override
    public String toString() {
        return "Text{" +
                "name='" + name + '\'' +
                '}';
    }
}
