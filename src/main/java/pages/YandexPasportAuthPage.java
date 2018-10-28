package pages;

import factories.ElementTitle;
import factories.PageEntry;
import factories.PageLoadMark;
import fields.Button;
import fields.Input;
import fields.Text;
import org.openqa.selenium.support.FindBy;

@PageEntry(title = "Яндекс.Паспорт - авторизация", url = "https://passport.yandex.ru")
public class YandexPasportAuthPage extends AbstractPage {

    @PageLoadMark
    @ElementTitle("Заголовок")
    @FindBy(xpath = "//body[@data-passporthost='\"passport.yandex.ru\"']//span[@class='passport-Icon passport-Icon_yandex_ru']")
    private Text pageBody;

    @ElementTitle("Поле <Логин или номер телефона>")
    @FindBy(xpath = ".//input[@name='login']")
    private Input login;

    @ElementTitle("Поле <Пароль>")
    @FindBy(xpath = ".//input[@name='passwd']")
    private Input password;

    @ElementTitle("Кнопка <Войти>")
    @FindBy(xpath = ".//button[@class='passport-Button']")
    private Button logInButton;

    @ElementTitle("Кнопка <Вернуться на сервис>")
    @FindBy(xpath = ".//div[@class='passport-Domik-Retpath']")
    private Text backToServise;

    @ElementTitle("Кнопка <Регистрация>")
    @FindBy(xpath = ".//a[text()='Регистрация']")
    private Text signInButton;
}
