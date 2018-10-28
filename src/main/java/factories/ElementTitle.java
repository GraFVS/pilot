package factories;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * WebElement`s title. Optional annotation.
 *
 * @author sbt-sidochenko-vv
 * @version $Id: $Id
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
