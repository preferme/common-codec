package houlei.common.codec.annotation;

import java.lang.annotation.*;
import java.util.ArrayList;
import java.util.Collection;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface FieldCollection {

    Class<? extends Collection> type() default ArrayList.class;

    Class<?> elementType();

}
