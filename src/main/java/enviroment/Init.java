package enviroment;

import com.codeborne.selenide.WebDriverRunner;
import factories.PageFactory;
import factories.WebDriverFactory;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;

public class Init {
    private static final Logger LOG = LoggerFactory.getLogger(Init.class);
    private static PageFactory pageFactory;

    public static WebDriver getWebDriver() {
        if (!driverStarted()) {
            initDriver();
            //movieCursorOutWorkPlace();        //Если нужно, чтобы курсор не мешал выполнению теста
        }
        return WebDriverRunner.getWebDriver();
    }

    public static PageFactory getPageFactory() {
        if (null == pageFactory) {
            pageFactory = new PageFactory();
        }
        return pageFactory;
    }

    public static boolean driverStarted() {
        return WebDriverRunner.hasWebDriverStarted();
    }

    public static void initDriver() {
        LOG.info("Инициализация веб-драйвера");
        WebDriverRunner.setWebDriver(WebDriverFactory.createDriver());
    }

    private static void movieCursorOutWorkPlace() {
        if (Boolean.valueOf(System.getProperty("sirius.selenium.headless", "false"))) return;
        try {
            Robot mouse = new Robot();
            mouse.mouseMove(0, 0);
        } catch (AWTException e) {
            LOG.debug("The cursor is not placed outside the workplace", e);
        }
    }
}
