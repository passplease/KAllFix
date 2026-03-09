package n1luik.KAllFix.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于添加 final 关键字
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.CLASS)
public @interface AddFinal {
}
