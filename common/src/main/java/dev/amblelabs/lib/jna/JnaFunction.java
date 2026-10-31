package dev.amblelabs.lib.jna;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface JnaFunction {
    /**
     * The JNA function name
     */
    String value() default "";
    Mapper mapper() default Mapper.DEFAULT;

    enum Mapper {
        NONE,
        DEFAULT,
        SNAKE_CASE
    }
}
