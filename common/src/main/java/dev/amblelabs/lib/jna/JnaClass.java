package dev.amblelabs.lib.jna;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface JnaClass {
    Mapper mapper() default Mapper.NONE;

    enum Mapper {
        NONE,
        SNAKE_CASE;

        public JnaFunction.Mapper asFunc() {
            return switch (this) {
                case NONE -> JnaFunction.Mapper.NONE;
                case SNAKE_CASE -> JnaFunction.Mapper.SNAKE_CASE;
            };
        }
    }
}
