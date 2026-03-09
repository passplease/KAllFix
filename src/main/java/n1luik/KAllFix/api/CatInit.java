package n1luik.KAllFix.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于标记初始化方法，但是是尾部，只会合并汇编局部变量不会
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.CLASS)
public @interface CatInit {
}
