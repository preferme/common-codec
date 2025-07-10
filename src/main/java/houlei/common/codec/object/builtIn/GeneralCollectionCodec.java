package houlei.common.codec.object.builtIn;

import houlei.common.codec.CodecException;
import houlei.common.codec.ObjectCodec;
import houlei.common.codec.factory.ObjectFactory;
import houlei.common.codec.factory.ObjectFactoryAware;
import houlei.common.codec.object.AbstractObjectCodec;

import java.nio.ByteBuffer;
import java.util.Collection;

import static houlei.common.codec.utils.ByteBufferUtil.expandByteBuffer;
import static houlei.common.codec.utils.ByteBufferUtil.checkRemaining;


public class GeneralCollectionCodec<E> extends AbstractObjectCodec<Collection<E>> implements ObjectCodec<Collection<E>>, ObjectFactoryAware {

    private ObjectFactory objectFactory;
    private Class<E> elementType;
    private ObjectCodec<E> elementCodec;

    @Override
    protected ByteBuffer encodeValue(ByteBuffer buffer, Collection<E> collection) throws CodecException {
        buffer = expandByteBuffer(buffer, 4);

        int size = collection.size();
        buffer.putInt(size);

        for (E element: collection) {
            buffer = elementCodec.encode(buffer, element);
        }

        return buffer;
    }

    @Override
    protected Collection<E> decodeValue(ByteBuffer buffer) throws CodecException {
        checkRemaining(buffer, 4);
        int size = buffer.getInt();

        Collection<E> collection = objectFactory.create(type);
        for (int i = 0; i < size; i++) {
            E element = (E) elementCodec.decode(buffer);
            collection.add(element);
        }

        return collection;
    }

    @Override
    public void setObjectFactory(ObjectFactory objectFactory) {
        this.objectFactory = objectFactory;
    }

    public Class<?> getElementType() {
        return elementType;
    }

    public void setElementType(Class<E> elementType) {
        this.elementType = elementType;
    }

    public void setElementCodec(ObjectCodec<E> elementCodec) {
        this.elementCodec = elementCodec;
    }

}
