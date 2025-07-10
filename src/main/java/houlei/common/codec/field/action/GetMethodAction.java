package houlei.common.codec.field.action;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class GetMethodAction<T, V> implements GetAction<T, V> {

    private final Method method;

    public GetMethodAction(Method method) {
        this.method = method;
    }

    @Override
    @SuppressWarnings("unchecked")
    public V execute(T obj) {
        try {
            return (V) method.invoke(obj, new Object[0]);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

}
