package pages;

import factories.ElementTitle;
import factories.PageEntry;
import factories.PageLoadMark;
import fields.Text;
import org.openqa.selenium.support.FindBy;

@PageEntry(title = "Яндекс.Паспорт - авторизация")
public class YandexPasportAuthPage extends AbstractPage {

    @PageLoadMark
    @ElementTitle("Заголовок")
    @FindBy(xpath = "//body[@data-passporthost='\"passport.yandex.ru\"']//span[@class='passport-Icon passport-Icon_yandex_ru']")
    private Text pageBody;

}
