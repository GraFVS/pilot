package factories;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import exception.AutotestError;
import fields.AbstractField;
import org.openqa.selenium.By;
import org.openqa.selenium.support.FindBy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pages.AbstractPage;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

/**
 * Класс служит для получения элементов объявленных на страницах.
 *
 * @author out-vetchinov-ms
 */
public class FieldFactory {
    private static final Logger LOG = LoggerFactory.getLogger(FieldFactory.class);

    public static AbstractField getPageLoadMark(Class<? extends AbstractPage> page) {
        return getField(f -> f.getAnnotation(PageLoadMark.class) != null, page).map(FieldFactory::getAbstractField).orElse(null);
    }

    public static AbstractField getField(String title, Class<? extends AbstractPage> page) {
        return getField(f -> f.getAnnotation(ElementTitle.class).value().equals(title), page)
                .map(FieldFactory::getAbstractField).orElseThrow(() ->
                        new AutotestError(String.format("Поле [%s] не объявлено на странице [%s]", title, page.getAnnotation(PageEntry.class))));
    }

    public static List<AbstractField> getFields(String title, Class<? extends AbstractPage> page) {
        return getField(f -> f.getAnnotation(ElementTitle.class).value().equals(title), page)
                .map(FieldFactory::getAbstractFields).orElseThrow(() ->
                        new AutotestError(String.format("Поле [%s] не объявлено на странице [%s]", title, page.getAnnotation(PageEntry.class))));
    }



    private static List<AbstractField> getAbstractFields(Field field) {
        By locator = By.xpath(field.getAnnotation(FindBy.class).xpath());
        LOG.debug("Выполняется поиск элементов по локатору {}", locator);
        ElementsCollection elements = $$(locator);
        return elements.stream().map(element -> {
            try {
                return (AbstractField) field.getType().getConstructor(SelenideElement.class,String.class)
                        .newInstance(element, field.getAnnotation(ElementTitle.class).value());
            } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
                throw new AutotestError("Ошибка создания списка объектов AbstractField", e);
            }
        }).collect(Collectors.toList());
    }

    private static AbstractField getAbstractField(Field field) {
        SelenideElement element = $(By.xpath(field.getAnnotation(FindBy.class).xpath()));
        try {
            return (AbstractField) field.getType().getConstructor(SelenideElement.class, String.class)
                    .newInstance(element, field.getAnnotation(ElementTitle.class).value());
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            throw new AutotestError("Ошибка создания объекта AbstractField", e);
        }
    }

    private static AbstractField getAbstractField(AbstractField parent, Field field) {
        SelenideElement element = parent.getSelenideElement().$(By.xpath(field.getAnnotation(FindBy.class).xpath()));
        try {
            return (AbstractField) field.getType().getConstructor(SelenideElement.class, String.class)
                    .newInstance(element, field.getAnnotation(ElementTitle.class).value());
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            throw new AutotestError("Ошибка создания объекта AbstractField", e);
        }
    }

    private static Optional<Field> getField(Predicate<Field> filter, Class<? extends AbstractPage> page) {
        List<Field> fields = new ArrayList<>();
        Collections.addAll(fields, page.getDeclaredFields());
        Arrays.stream(page.getAnnotation(PageEntry.class).afilliatedPages())
                .flatMap(aClass -> Arrays.stream(aClass.getDeclaredFields()))
                .forEach(fields::add);
        return fields.stream().filter(filter).findFirst();
    }
}
