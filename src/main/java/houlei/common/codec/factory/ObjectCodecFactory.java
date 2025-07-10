package houlei.common.codec.factory;

import houlei.common.codec.ObjectCodec;
import houlei.common.codec.annotation.*;
import houlei.common.codec.annotation.Object;
import houlei.common.codec.field.FieldRecordsCodec;
import houlei.common.codec.field.action.*;
import houlei.common.codec.object.DefaultObjectCodec;
import houlei.common.codec.field.FieldRecord;
import houlei.common.codec.object.builtIn.*;
import houlei.common.codec.utils.ReflectUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;


public final class ObjectCodecFactory implements ObjectFactoryAware {

    private final HashMap<Class<?>, ObjectCodec<?>> codecCache = new HashMap<>();

    private ObjectFactory objectFactory = ObjectFactory.DEFAULT;

    public ObjectCodecFactory() {
        for (Primitive prim : Primitive.values()) {
            codecCache.put(prim.getType(), prim);
        }
        for (PrimitiveWrapped prim : PrimitiveWrapped.values()) {
            codecCache.put(prim.getType(), prim);
        }

        StringCodec stringCodec = new StringCodec();
        stringCodec.setClassId(ClassIds.String);
        stringCodec.setType(String.class);
        codecCache.put(stringCodec.getType(), stringCodec);
    }

    public <T> ObjectCodec<T> create(Class<T> objectType) {
        if (codecCache.containsKey(objectType)) {
            throw new IllegalArgumentException("class " + objectType.getName() + " is already registered");
        }

        // 1. parse object type class
        // 1.1 parse classId
        Object annObject = objectType.getAnnotation(Object.class);
        if (annObject == null) {
            throw new IllegalArgumentException(objectType.getName() + " is not annotated with @Object");
        }

        int classId = annObject.classId();
        if (classId <= ClassIds.OBJECT_NULL) {
            throw new IllegalArgumentException("classId in @Object must be a non-zero positive integer");
        }

        // 1.2 parse fields
        FieldRecordsCodec<T> fieldRecordsCodec = createFieldRecordsCodec(objectType);

        DefaultObjectCodec<T> codec = new DefaultObjectCodec<>();
        codec.setClassId(classId);
        codec.setType(objectType);
        codec.setObjectFactory(objectFactory);
        codec.setFieldRecordsCodec(fieldRecordsCodec);

        codecCache.put(objectType, codec);
        return codec;
    }

    <T> FieldRecordsCodec<T> createFieldRecordsCodec(Class<T> objectType) {
        Field[] fields = objectType.getDeclaredFields();
        ArrayList<FieldRecord<T, ?>> fieldRecords = new ArrayList<>();
        for (Field field : fields) {
            if (field.isAnnotationPresent(Ignore.class)) {
                continue;
            }

            // getter
            Method getter = ReflectUtil.findGetter(field);
            GetAction<T, ?> getAction = getter != null ? new GetMethodAction<>(getter) : new GetFieldAction<>(field);

            // setter
            Method setter = ReflectUtil.findSetter(field);
            SetAction<T, ?> setAction = setter != null ? new SetMethodAction<>(setter) : new SetFieldAction<>(field);

            // field codec
            ObjectCodec<?> filedCodec = makeFieldObjectCodec(objectType, field);

            FieldRecord<T, ?> record = new FieldRecord(field, getAction, setAction, filedCodec);
            fieldRecords.add(record);
        }

        FieldRecordsCodec<T> fieldRecordsCodec = new FieldRecordsCodec<>();
        fieldRecordsCodec.setFieldRecords(fieldRecords);

        return fieldRecordsCodec;
    }

    @SuppressWarnings("unchecked")
    private <T> ObjectCodec<?> makeFieldObjectCodec(Class<T> objectType, Field field) {
        Class<?> fieldType = field.getType();
        ObjectCodec<?> filedCodec;
        if (Collection.class.isAssignableFrom(fieldType)) {
            filedCodec = makeGeneralCollectionCodec(objectType, field, (Class<? extends Collection>) fieldType);
        } else if (Map.class.isAssignableFrom(fieldType)) {
            filedCodec = makeGeneralMapCodec(objectType, field, (Class<? extends Map>) fieldType);
        } else if (fieldType.isArray()) {
            filedCodec = makeArrayCodec(objectType, field, fieldType);
        } else if (fieldType.isEnum()) {
            EnumCodec codec = new EnumCodec();
            codec.setClassId(ClassIds.Enum);
            codec.setType((Class<Enum<?>>) fieldType);
            filedCodec = codec;
        } else {
            Class<?> fieldType_ = fieldType;
            if (field.isAnnotationPresent(houlei.common.codec.annotation.Field.class)) {
                houlei.common.codec.annotation.Field annoField = field.getAnnotation(houlei.common.codec.annotation.Field.class);
                fieldType_ = annoField.type() == void.class ? fieldType : annoField.type();
            }
            if (codecCache.containsKey(fieldType_)) {
                filedCodec = codecCache.get(fieldType_);
            } else {
                if (!ReflectUtil.checkInstantiable(fieldType_)) {
                    throw new IllegalArgumentException(String.format("field (%s) is not instantiable", field.getName()));
                }
                if (fieldType_.equals(objectType)) {
                    // TODO field类型 与 对象的类型 相同，与循环引用相似，暂时不支持。
                    throw new IllegalArgumentException("fieldType is the same as the objectType");
                }
                filedCodec = codecCache.get(fieldType_);
            }
        }
        return filedCodec;
    }

    @SuppressWarnings("unchecked")
    private <T, C> ArrayCodec<T, C> makeArrayCodec(Class<T> objectType, Field field, Class<?> fieldType) {
        Class<?> componentType = fieldType.getComponentType();
        if (field.isAnnotationPresent(FieldArray.class)) {
            FieldArray annoField = field.getAnnotation(FieldArray.class);
            componentType = annoField.componentType();
        }
        ArrayCodec<T, C> codec = new ArrayCodec<>();
        codec.setClassId(ClassIds.Array);
        codec.setType((Class<T>) fieldType);
        codec.setComponentType((Class<C>) componentType);
        if (codecCache.containsKey(componentType)) {
            codec.setComponentCodec((ObjectCodec<C>) codecCache.get(componentType));
        } else if (componentType.isEnum()){
            EnumCodec enumCodec = new EnumCodec();
            enumCodec.setClassId(ClassIds.Enum);
            enumCodec.setType((Class<Enum<?>>) componentType);
            codec.setComponentCodec((ObjectCodec<C>) enumCodec);
        } else {
            if (!ReflectUtil.checkInstantiable(componentType)) {
                throw new IllegalArgumentException(String.format("componentType (%s) in field (%s) is not instantiable", componentType == null ? "null" : componentType.getSimpleName(), field.getName()));
            }
            if (componentType.equals(objectType)) {
                // TODO 集合的元素类型 与 对象的类型 相同，与循环引用相似，暂时不支持。
                throw new IllegalArgumentException("componentType is the same as the objectType");
            }
            ObjectCodec<C> componentCodec = (ObjectCodec<C>) create(componentType);
            codec.setComponentCodec(componentCodec);
        }
        return codec;
    }

    @SuppressWarnings("unchecked")
    private <T, K, V> GeneralMapCodec<K, V> makeGeneralMapCodec(Class<T> objectType, Field field, Class<? extends Map> fieldType) {
        Class<? extends Map<K, V>> fieldType_ = null;
        Class<K> keyType_ = null;
        Class<V> valueType_ = null;
        if (field.isAnnotationPresent(FieldMap.class)) {
            FieldMap fieldMap = field.getAnnotation(FieldMap.class);
            fieldType_ = (Class<? extends Map<K, V>>) fieldMap.type();
            keyType_ = (Class<K>) fieldMap.keyType();
            valueType_ = (Class<V>) fieldMap.valueType();
        }
        if (ReflectUtil.checkInstantiable(fieldType)) {
            fieldType_ = (Class<? extends Map<K, V>>) fieldType;
        }
        if (!ReflectUtil.checkInstantiable(fieldType_)) {
            throw new IllegalArgumentException(String.format("field (%s) is not instantiable", field.getName()));
        }
        if (!ReflectUtil.checkInstantiable(keyType_)) {
            throw new IllegalArgumentException(String.format("keyType (%s) in field (%s) is not instantiable", keyType_ == null ? "null" : keyType_.getSimpleName(), field.getName()));
        }
        if (!ReflectUtil.checkInstantiable(valueType_)) {
            throw new IllegalArgumentException(String.format("valueType (%s) in field (%s) is not instantiable", valueType_ == null ? "null" : valueType_.getSimpleName(), field.getName()));
        }
        GeneralMapCodec<K, V> codec = new GeneralMapCodec<>();
        codec.setClassId(ClassIds.GeneralMap);
        codec.setType((Class<Map<K, V>>) fieldType_);
        codec.setKeyType(keyType_);
        codec.setValueType(valueType_);
        codec.setObjectFactory(objectFactory);
        if (keyType_.equals(objectType)) {
            // TODO Map的Key类型 与 对象的类型 相同，与循环引用相似，暂时不支持。
            throw new IllegalArgumentException("keyType is the same as the objectType");
        }
        if (valueType_.equals(objectType)) {
            // TODO Map的Value类型 与 对象的类型 相同，与循环引用相似，暂时不支持。
            throw new IllegalArgumentException("valueType is the same as the objectType");
        }
        ObjectCodec<K> keyCodec = codecCache.containsKey(keyType_) ? (ObjectCodec<K>) codecCache.get(keyType_) : create(keyType_);
        ObjectCodec<V> valueCodec = codecCache.containsKey(valueType_) ? (ObjectCodec<V>) codecCache.get(valueType_) : create(valueType_);
        codec.setKeyCodec(keyCodec);
        codec.setValueCodec(valueCodec);
        return codec;
    }

    @SuppressWarnings("unchecked")
    private <T, E> GeneralCollectionCodec<E> makeGeneralCollectionCodec(Class<T> objectType, Field field, Class<? extends Collection> fieldType) {
        Class<? extends Collection<E>> fieldType_ = null;
        Class<E> elementType_ = null;
        if (field.isAnnotationPresent(FieldCollection.class)) {
            FieldCollection fieldCollection = field.getAnnotation(FieldCollection.class);
            fieldType_ = (Class<? extends Collection<E>>) fieldCollection.type();
            elementType_ = (Class<E>) fieldCollection.elementType();
        }
        if (field.isAnnotationPresent(FieldSet.class)) {
            FieldSet fieldSet = field.getAnnotation(FieldSet.class);
            fieldType_ = (Class<? extends Collection<E>>) fieldSet.type();
            elementType_ = (Class<E>) fieldSet.elementType();
        }
        if (field.isAnnotationPresent(FieldList.class)) {
            FieldList fieldList = field.getAnnotation(FieldList.class);
            fieldType_ = (Class<? extends Collection<E>>) fieldList.type();
            elementType_ = (Class<E>) fieldList.elementType();
        }
        if (ReflectUtil.checkInstantiable(fieldType)) {
            fieldType_ = (Class<? extends Collection<E>>) fieldType;
        }
        if (!ReflectUtil.checkInstantiable(fieldType_)) {
            throw new IllegalArgumentException(String.format("field (%s) is not instantiable", field.getName()));
        }
        if (Objects.isNull(elementType_)) {
            throw new IllegalArgumentException(String.format("elementType in field (%s) is null", field.getName()));
        }
        if (!codecCache.containsKey(elementType_) && !ReflectUtil.checkInstantiable(elementType_)) {
            throw new IllegalArgumentException(String.format("elementType (%s) in field (%s) is not instantiable", elementType_.getSimpleName(), field.getName()));
        }
        GeneralCollectionCodec<E> codec = new GeneralCollectionCodec<>();
        codec.setClassId(ClassIds.GeneralCollection);
        codec.setType((Class<Collection<E>>) fieldType_);
        codec.setElementType(elementType_);
        codec.setObjectFactory(objectFactory);
        if (elementType_.equals(objectType)) {
            // TODO 集合的元素类型 与 对象的类型 相同，与循环引用相似，暂时不支持。
            throw new IllegalArgumentException("elementType is the same as the objectType");
        }
        ObjectCodec<E> elementCodec = codecCache.containsKey(elementType_) ? (ObjectCodec<E>) codecCache.get(elementType_) : create(elementType_);
        codec.setElementCodec(elementCodec);
        return codec;
    }

    @Override
    public void setObjectFactory(ObjectFactory objectFactory) {
        this.objectFactory = objectFactory;
    }

}
