package houlei.common.codec.packet;

import houlei.common.codec.PacketCodec;
import houlei.common.codec.factory.ObjectFactory;
import houlei.common.codec.factory.ObjectFactoryAware;
import houlei.common.codec.field.FieldRecordsCodec;

import java.nio.ByteBuffer;


public class DefaultPacketCodec<T> extends AbstractPacketCodec<T> implements PacketCodec<T>, ObjectFactoryAware {

    private ObjectFactory objectFactory = ObjectFactory.DEFAULT;
    private FieldRecordsCodec<T> fieldRecordsCodec;

    @Override
    public void setObjectFactory(ObjectFactory objectFactory) {
        this.objectFactory = objectFactory;
    }

    @Override
    protected ByteBuffer encodeBody(ByteBuffer buffer, T body) {
        return fieldRecordsCodec.encodeFields(buffer, body);
    }

    @Override
    protected T decodeBody(ByteBuffer buffer) {
        T body = objectFactory.create(type);
        fieldRecordsCodec.decodeFields(buffer, body);
        return body;
    }

    public void setFieldRecordsCodec(FieldRecordsCodec<T> fieldRecordsCodec) {
        this.fieldRecordsCodec = fieldRecordsCodec;
    }

}
