package houlei.common.codec.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class ReflectUtil {

    public static Method findGetter(Field field, String getterName) {
        try {
            Method getter = field.getDeclaringClass().getDeclaredMethod(getterName, new Class[0]);
            return field.getType().equals(getter.getReturnType()) ? getter : null;
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    public static Method findGetter(Field field) {
        String fieldName = field.getName();
        String getterName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
        return findGetter(field, getterName);
    }

    public static Method findSetter(Field field) {
        Class<?> classType = field.getDeclaringClass();
        String fieldName = field.getName();
        String setterName = "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
        try {
            Method setter = classType.getDeclaredMethod(setterName, new Class[]{field.getType()});
            return void.class.equals(setter.getReturnType()) ? setter : null;
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    public static boolean checkInstantiable(Class<?> clazz) {
        if (clazz != null && !clazz.isInterface() && !Modifier.isAbstract(clazz.getModifiers())) {
            try {
                clazz.getDeclaredConstructor(new Class[0]);
                return true;
            } catch (NoSuchMethodException ignored) {
            }
        }
        return false;
    }
}
