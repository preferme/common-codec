package houlei.common.codec.field.action;

import java.lang.reflect.Field;
import java.util.Objects;

public class SetFieldAction<T, V> implements SetAction<T, V> {

    private final Field field;

    public SetFieldAction(Field field) {
        this.field = field;
        if (Objects.isNull(field)) {
            throw new IllegalArgumentException("field is null");
        }
    }

    @Override
    public void execute(T obj, V value) {
        try {
            if (!field.canAccess(obj)) {
                field.setAccessible(true);
            }
            field.set(obj, value);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

}
