package houlei.common.codec.object.builtIn;

import houlei.common.codec.CodecException;
import houlei.common.codec.ObjectCodec;
import houlei.common.codec.object.AbstractObjectCodec;

import java.lang.reflect.Array;
import java.nio.ByteBuffer;

import static houlei.common.codec.utils.ByteBufferUtil.expandByteBuffer;
import static houlei.common.codec.utils.ByteBufferUtil.checkRemaining;


public class ArrayCodec<T, C> extends AbstractObjectCodec<T> implements ObjectCodec<T> {

    private Class<C> componentType;
    private ObjectCodec<C> componentCodec;

    @Override
    @SuppressWarnings("unchecked")
    protected ByteBuffer encodeValue(ByteBuffer buffer, T obj) throws CodecException {
        int length = Array.getLength(obj);
        buffer = expandByteBuffer(buffer, 4);
        buffer.putInt(length);
        for (int i = 0; i < length; i++) {
            C component = (C) Array.get(obj, i);
            buffer = componentCodec.encode(buffer, component);
        }
        return buffer;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected T decodeValue(ByteBuffer buffer) throws CodecException {
        checkRemaining(buffer, 4);
        int length = buffer.getInt();
        T array = (T) Array.newInstance(componentType, length);
        for (int i = 0; i < length; i++) {
            Array.set(array, i, componentCodec.decode(buffer));
        }
        return array;
    }

    public void setComponentType(Class<C> componentType) {
        this.componentType = componentType;
    }

    public void setComponentCodec(ObjectCodec<C> componentCodec) {
        this.componentCodec = componentCodec;
    }

}
