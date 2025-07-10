package houlei.common.codec.field.action;

public interface SetAction<T, V> {

    void execute(T obj, V value);

}
