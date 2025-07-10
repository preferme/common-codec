package houlei.common.codec.annotation;

import java.lang.annotation.*;
import java.util.HashMap;
import java.util.Map;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface FieldMap {

    Class<? extends Map> type() default HashMap.class;

    Class<?> keyType();

    Class<?> valueType();

}
