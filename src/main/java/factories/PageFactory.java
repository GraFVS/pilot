package factories;

import enviroment.Init;
import exception.AutotestError;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pages.AbstractPage;
import org.reflections.Reflections;
import pages.Frames;

import java.util.HashMap;
import java.util.Map;


/**
 * <p>
 * PageFactory class.</p>
 *
 * @author sbt-sidochenko-vv
 * @version $Id: $Id
 */
public class PageFactory {

    private static AbstractPage currentPage;
    private static final Map<String, Class<? extends AbstractPage>> PAGES_CACHE = new HashMap<>();
    private static final Logger LOG = LoggerFactory.getLogger(PageFactory.class);
    private static final int WAIT_ACTIVE_FRAME = 10;
    private static String currentPageName;


    private <T> T getPage(Class<T> clazz, Boolean... checkError) {
        final T page = construct(clazz);
        set((AbstractPage) page);
        return page;
    }


    static {
        Reflections reflections = new Reflections("pages");
        for (Class<? extends AbstractPage> clazz : reflections.getSubTypesOf(AbstractPage.class)) {
            PageEntry entry = clazz.getAnnotation(PageEntry.class);
            if (entry == null) {
                LOG.error("Страница [" + clazz + "] не содержит аннотации @PageEntry. Данная страница будет недоступна при поиске.");
                continue;
            }
            PAGES_CACHE.put(entry.title(), clazz);
        }
    }

    //todo Добавить проверку дублирования названий полей
    public AbstractPage getPage(String name) {
        currentPageName = name;
        return get(name);
    }

    public String getCurrentPageName() {
        return currentPageName;
    }

    public AbstractPage getCurrentPage() {
        return currentPage;
    }


    private static void set(AbstractPage page) {
        currentPage = page;
    }

    private AbstractPage get(String name, Boolean... checkError) {
        final Class<? extends AbstractPage> clazz = PAGES_CACHE.get(name);
        if (clazz == null) {
            throw new NullPointerException("Страница \"" + name + "\" не найдена.");
        }
        return getPage(clazz, checkError);
    }

    /**
     * Проверяем указание фрейма в аннотации @PageEntry (если не указана- по-умолчанию STANDART_ACTIVE_FRAME);
     * Если указанный фрейм был обнаружен - фокусим драйвер на него;
     * Если фрейм указан "TOP_WINDOW" или не был обнаружен - переключаем драйвер на основной контент;
     * в результате возвращаем экземпяр страницы фрейма
     *
     * @param clazz
     * @param <T>
     * @return
     */
    private <T> T construct(Class<T> clazz) {
        try {
            WebDriver driver = Init.getWebDriver();
            Frames targetFrame = clazz.getAnnotation(PageEntry.class).frame();
            Init.getWebDriver().switchTo().defaultContent();
            if (targetFrame != Frames.TOP_WINDOW){
                try {
                    WebDriverWait wait = new WebDriverWait(driver, (long) WAIT_ACTIVE_FRAME);
                    wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(By.xpath(targetFrame.getXpath())));
                } catch (org.openqa.selenium.TimeoutException e) {
                    LOG.debug("Фрейм не обнаружен, драйвер переключен на основной контент");
                }
            }
            return clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new AutotestError("При создании страницы произошла ошибка.", e);
        }
    }
}
