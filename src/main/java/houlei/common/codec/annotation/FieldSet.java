package houlei.common.codec.annotation;

import java.lang.annotation.*;
import java.util.HashSet;
import java.util.Set;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface FieldSet {

    Class<? extends Set> type() default HashSet.class;

    Class<?> elementType();

}
