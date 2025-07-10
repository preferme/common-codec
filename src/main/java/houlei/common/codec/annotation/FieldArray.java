package houlei.common.codec.annotation;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface FieldArray {

    Class<?> componentType() default void.class;

}
