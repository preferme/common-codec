package houlei.common.codec.field.action;

import java.lang.reflect.Field;
import java.util.Objects;

public class GetFieldAction<T, V> implements GetAction<T, V> {

    private final Field field;

    public GetFieldAction(Field field) {
        this.field = field;
        if (Objects.isNull(field)) {
            throw new IllegalArgumentException("field is null");
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public V execute(T obj) {
        try {
            if (!field.canAccess(obj)) {
                field.setAccessible(true);
            }
            return (V) field.get(obj);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
