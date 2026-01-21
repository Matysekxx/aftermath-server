package cz.matysekxx.aftermathserver.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.METHOD, ElementType.TYPE, ElementType.LOCAL_VARIABLE})
public @interface Unused {
    String value() default "This property is currently unused";
}
