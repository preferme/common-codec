package houlei.common.codec.object;

import houlei.common.codec.CodecException;
import houlei.common.codec.ObjectCodec;
import houlei.common.codec.factory.ObjectFactory;
import houlei.common.codec.factory.ObjectFactoryAware;
import houlei.common.codec.field.FieldRecord;
import houlei.common.codec.field.FieldRecordsCodec;

import java.nio.ByteBuffer;
import java.util.List;


public class DefaultObjectCodec<T> extends AbstractObjectCodec<T> implements ObjectCodec<T>, ObjectFactoryAware {

    private ObjectFactory objectFactory = ObjectFactory.DEFAULT;
    private FieldRecordsCodec<T> fieldRecordsCodec = new FieldRecordsCodec<>();

    @Override
    protected ByteBuffer encodeValue(ByteBuffer byteBuffer, T value) throws CodecException {
        return fieldRecordsCodec.encodeFields(byteBuffer, value);
    }

    @Override
    protected T decodeValue(ByteBuffer buffer) {
        T obj = objectFactory.create(type);
        fieldRecordsCodec.decodeFields(buffer, obj);
        return obj;
    }

    @Override
    public void setObjectFactory(ObjectFactory objectFactory) {
        this.objectFactory = objectFactory;
    }

    public void setFieldRecordsCodec(FieldRecordsCodec<T> fieldRecordsCodec) {
        this.fieldRecordsCodec = fieldRecordsCodec;
    }

}
