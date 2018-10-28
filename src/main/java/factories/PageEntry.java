package factories;

import pages.AbstractPage;
import pages.Frames;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author out-vetchinov-ms
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface PageEntry {
    /**
     * Название страницы
     */
    String title();

    /**
     * url страницы - по-умолчанию отсутствует
     */
    String url() default "";

    /**
     * Указывается iframe, в котором находятся элементы страницы, по-умолчанию - активный фрейм
     */
    Frames frame() default Frames.TOP_WINDOW;

    /**
     * Массив связанных страниц, в которых также будет осуществлятся поиск элемента: подвал, боковая панель навигации и т.д.
     */
    Class<? extends AbstractPage>[] afilliatedPages() default {};
}