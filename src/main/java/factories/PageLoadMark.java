package factories;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Аннотацией помечается поле, которое должно отображаться, чтобы страница считалась загруженной.
 * Алгоритм проверки загрузки любой страницы можно переопределить в методе isOpened().
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface PageLoadMark {

}
