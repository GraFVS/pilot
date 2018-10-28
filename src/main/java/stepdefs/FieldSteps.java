package stepdefs;

import cucumber.api.DataTable;
import cucumber.api.java.ru.И;
import cucumber.api.java.ru.Тогда;
import exception.Verify;
import fields.AbstractField;
import io.qameta.allure.Step;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.ActionWait;

import java.util.concurrent.TimeUnit;

import static enviroment.Init.getCurrentPage;
import static java.time.temporal.ChronoUnit.SECONDS;
import static stepdefs.UtilSteps.freeze;

public class FieldSteps {

    private static final Logger LOG = LoggerFactory.getLogger(FieldSteps.class);

    @И("^поле \"([^\"]*)\" заполняется значением \"([^\"]*)\"$")
    @Step
    public void fillUpField(String fieldTitle, String value) {
//        String variable = getVariable(value);     //TODO переделать под Evaluator после прикручивания
        String variable = value;
        getCurrentPage().getField(fieldTitle).clear();
        getCurrentPage().getField(fieldTitle).fillUp(variable);
        freeze(1, SECONDS);
        LOG.debug("Поле {} заполнено значением {}", fieldTitle, variable);
    }

    @И("^заполняются поля:$")
    @Step
    public void fillUpFields(DataTable dataTable) {
        dataTable.asLists(String.class).forEach(row -> fillUpField(row.get(0), row.get(1)));
    }

    //TODO: раскомментировать после прикручивания Evaluator-а
//    @И("^заполняются поля если переменная не пустая:$")
//    @Step
//    public void filtredFillUpFields(DataTable dataTable) {
//        for (List<String> row : dataTable.asLists(String.class)) {
//            if (getVariable(row.get(1)).equals("null")) {
//                LOG.debug("Значение переменной {} отсутстует в стеше, элемент {} не будет заполнен", row.get(1), row.get(0));
//            } else fillUpField(row.get(0), row.get(1));
//        }
//    }


    @И("^(не |)отображается (?:поле|кнопка) \"([^\"]*)\"$")
    @Step
    public void fieldIsDisplayed(String visible, String fieldTitle) {
        boolean isVisible = visible.isEmpty();
        Assert.assertEquals(String.format("Поле [%s] %sне отображено на странице", fieldTitle, visible), isVisible, getCurrentPage().getField(fieldTitle).isDisplayed(isVisible));
    }

    @И("^(не |)отображаются (?:поля|кнопки|элементы):$")
    @Step
    public void fieldsAreDisplayed(String visible, DataTable dataTable) {
        dataTable.asLists(String.class).forEach(row -> fieldIsDisplayed(visible, row.get(0)));
    }


    //TODO: После доб Evaluator заменить "fieldTitle" на "getVariable(fieldTitle)"
    @И("^(?:поле|элемент) \"([^\"]*)\" (?:доступно|доступен) для редактирования$")
    @Step
    public void fieldIsEnabled(String fieldTitle) {
        Assert.assertTrue(String.format("Поле [%s] не доступно для редактирования", fieldTitle),
                getCurrentPage().getField(fieldTitle).isEnabled());
    }

    //TODO: После доб Evaluator заменить "fieldTitle" на "getVariable(fieldTitle)"
    @И("^(?:поле|элемент) \"([^\"]*)\" не (?:доступно|доступен) для редактирования$")
    @Step
    public void fieldIsNotEnabled(String fieldTitle) {
        Assert.assertFalse(
                String.format("Поле [%s] доступно для редактирования", fieldTitle),
                getCurrentPage().getField(fieldTitle).isEnabled());
    }

    @И("^(?:поля|элементы) доступны для редактирования:$")
    @Step
    public void fieldsAreEnabled(DataTable dataTable) {
        dataTable.asLists(String.class).forEach(row -> fieldIsEnabled(row.get(0)));
    }

    @И("^(?:поля|элементы) не доступны для редактирования:$")
    @Step
    public void fieldsAreNotEnabled(DataTable dataTable) {
        dataTable.asLists(String.class).forEach(row -> fieldIsNotEnabled(row.get(0)));
    }

    @Тогда("значение поля \"([^\"]*)\" равно \"(.*)\"$")
    @Step
    public void fieldValueEqualsTo(String fieldTitle, String value) {
        //String variable = getVariable(value);
        LOG.debug("Проверям, что значение поля {} равно {}", fieldTitle, value);
        Assert.assertEquals(value, getCurrentPage().getField(fieldTitle).getValue().replaceAll("\u00a0", " ").trim());
    }

    @Тогда("значения полей равны:$")
    @Step
    public void fieldsValuesEqualTo(DataTable dataTable) {
        dataTable.asLists(String.class).forEach(row -> fieldValueEqualsTo(row.get(0), row.get(1)));
    }

    @И("^нажимается (?:кнопка|поле|элемент) \"([^\"]*)\"$")
    @Step
    public void clickField(String fieldTitle) {
        getCurrentPage().getField(fieldTitle).click();
    }

    @И("^очищается (?:поле|элемент) \"([^\"]*)\"$")
    @Step
    public void clearField(String fieldTitle) {
        getCurrentPage().getField(fieldTitle).clear();
    }

    @И("^курсор наводится на (?:кнопку|поле|элемент) \"([^\"]*)\"$")
    @Step
    public void hoverField(String fieldTitle) {
        getCurrentPage().getField(fieldTitle).hover();
    }

    @Тогда("текст поля \"([^\"]*)\" равен \"(.*)\"$")
    @Step
    public void fieldTextEqualsTo(String fieldTitle, String value) {
       Verify.verifyTrue("Текст полей не совпадает!",
                value.equals(
                getCurrentPage()
                        .getField(fieldTitle)
                        .getText()
                        //TODO: move to generic method
                        .replaceAll("\\n|\\r\\n", " ")
                        .replaceAll("\u00a0", " ")
                        .replaceAll("\\s+", " ")
                        .trim()));
    }

    @Тогда("текст поля \"([^\"]*)\" заполнен$")
    @Step
    public void fieldTextNotEmpty(String fieldTitle) {
        Assert.assertFalse(String.format("Поле [%s] не заполнено", fieldTitle),
                getCurrentPage().getField(fieldTitle).getText().isEmpty());
    }

    @Тогда("текст поля \"([^\"]*)\" изменился на \"([^\"]*)\"$")
    @Step
    public void fieldTextChangeTo(String fieldTitle, String value) {
        Object expectedText = value;
        AbstractField field = getCurrentPage().getField(fieldTitle);
        ActionWait.shortTimeout().pollingEvery(500, TimeUnit.MILLISECONDS)
                .safeCall(webDriver -> field.getText().equals(expectedText));
        Assert.assertEquals(expectedText, getCurrentPage().getField(fieldTitle).getText());
    }

    //TODO: раскомментировать после прикручивания Evaluator-а
//    @Тогда("значение поля \"([^\"]*)\" сохраняется в переменной \"([^\"]*)\"$")
//    @Step
//    public void saveFieldValue(String fieldTitle, String variable) {
//        String value = getCurrentPage().getField(fieldTitle).getValue();
//        setVariable(variable, value);
//        LOG.info("Значение {} поля {} сохранено в переменной {}", value, fieldTitle, variable);
//    }

    //TODO: раскомментировать после прикручивания Evaluator-а
//    @Тогда("текст поля \"([^\"]*)\" сохраняется в переменной \"([^\"]*)\"$")
//    @Step
//    public void saveFieldText(String fieldTitle, String variable) {
//        String text = getCurrentPage().getField(fieldTitle).getText();
//        setVariable(variable, text);
//        LOG.info("Текст {} поля {} сохранено в переменной {}", text, fieldTitle, variable);
//    }

    @Тогда("^текст полей равен$")
    @Step
    public void текстПолейРавен(DataTable table) {
        int fieldCol = 0;
        int valueCol = 1;
        table.asLists(String.class).forEach(row -> fieldTextEqualsTo(row.get(fieldCol), row.get(valueCol)));
    }

}