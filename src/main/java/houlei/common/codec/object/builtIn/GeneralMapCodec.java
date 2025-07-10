package houlei.common.codec.object.builtIn;

import houlei.common.codec.CodecException;
import houlei.common.codec.ObjectCodec;
import houlei.common.codec.factory.ObjectFactory;
import houlei.common.codec.factory.ObjectFactoryAware;
import houlei.common.codec.object.AbstractObjectCodec;

import java.nio.ByteBuffer;
import java.util.Map;

import static houlei.common.codec.utils.ByteBufferUtil.expandByteBuffer;
import static houlei.common.codec.utils.ByteBufferUtil.checkRemaining;


public class GeneralMapCodec<K, V> extends AbstractObjectCodec<Map<K, V>> implements ObjectCodec<Map<K, V>>, ObjectFactoryAware {

    private ObjectFactory objectFactory;
    private Class<K> keyType;
    private Class<V> valueType;
    private ObjectCodec<K> keyCodec;
    private ObjectCodec<V> valueCodec;

    @Override
    protected ByteBuffer encodeValue(ByteBuffer buffer, Map<K, V> map) throws CodecException {
        buffer = expandByteBuffer(buffer, 4);

        int size = map.size();
        buffer.putInt(size);

        for (Map.Entry<K, V> entry : map.entrySet()) {
            buffer = keyCodec.encode(buffer, entry.getKey());
            buffer = valueCodec.encode(buffer, entry.getValue());
        }

        return buffer;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected Map<K, V> decodeValue(ByteBuffer buffer) throws CodecException {
        checkRemaining(buffer, 4);
        int size = buffer.getInt();

        Map<K, V> map = (Map<K, V>) objectFactory.create(type);
        for (int i = 0; i < size; i++) {
            K key = keyCodec.decode(buffer);
            V value = valueCodec.decode(buffer);
            map.put(key, value);
        }

        return Map.of();
    }

    @Override
    public void setObjectFactory(ObjectFactory objectFactory) {
        this.objectFactory = objectFactory;
    }

    public Class<K> getKeyType() {
        return keyType;
    }

    public Class<V> getValueType() {
        return valueType;
    }

    public void setKeyType(Class<K> keyType) {
        this.keyType = keyType;
    }

    public void setValueType(Class<V> valueType) {
        this.valueType = valueType;
    }

    public void setKeyCodec(ObjectCodec<K> keyCodec) {
        this.keyCodec = keyCodec;
    }

    public void setValueCodec(ObjectCodec<V> valueCodec) {
        this.valueCodec = valueCodec;
    }

}
