package houlei.common.codec.field.action;


public interface GetAction<T, V> {

    V execute(T obj);

}
