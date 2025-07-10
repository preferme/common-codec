package houlei.common.codec.field;

import houlei.common.codec.ObjectCodec;
import houlei.common.codec.field.action.GetAction;
import houlei.common.codec.field.action.SetAction;

import java.lang.reflect.Field;

public record FieldRecord<T, F>(
        Field field,
        GetAction<T, F> getAction,
        SetAction<T, F> setAction,
        ObjectCodec<F> fieldCodec) {

}
