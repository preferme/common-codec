package houlei.common.codec.annotation;

import java.lang.annotation.*;
import java.util.ArrayList;
import java.util.List;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface FieldList {

    Class<? extends List> type() default ArrayList.class;

    Class<?> elementType();

}
