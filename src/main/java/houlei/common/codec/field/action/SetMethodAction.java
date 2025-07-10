package houlei.common.codec.field.action;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class SetMethodAction<T, V> implements SetAction<T, V> {

    private final Method method;

    public SetMethodAction(Method method) {
        this.method = method;
    }

    @Override
    public void execute(T obj, V value) {
        try {
            method.invoke(obj, new Object[]{value});
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

}
