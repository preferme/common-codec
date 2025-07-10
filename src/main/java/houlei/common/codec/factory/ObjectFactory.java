package houlei.common.codec.factory;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.WeakHashMap;

public interface ObjectFactory {

    <T> T create(Class<T> type);


    ObjectFactory DEFAULT = new ObjectFactory() {

        private static final WeakHashMap<Class<?>, MethodHandle> constructorCache = new WeakHashMap<>();

        @Override
        @SuppressWarnings({"unchecked"})
        public <T> T create(Class<T> type) {
            MethodHandle constructor = constructorCache.get(type);
            if (constructor == null) {
                try {
                    constructor = MethodHandles.lookup().findConstructor(type, MethodType.methodType(void.class));
                } catch (NoSuchMethodException | IllegalAccessException e) {
                    throw new RuntimeException(e.getMessage(), e);
                }
                constructorCache.put(type, constructor);
            }
            try {
                return (T) constructor.invokeWithArguments();
            } catch (Throwable e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        }
    };
}
