package factories;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Аннотация ElementTitle - заголовок веб-элемента.
 * Используется для обращения к элементам на странице в cucumber-feature-файлах
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ElementTitle {

    /**
     * Title text
     *
     * @return a {@link String} object.
     */
    String value();
    String subElementOf() default "";
}
