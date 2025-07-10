package houlei.common.codec.object.builtIn;

import houlei.common.codec.CodecException;
import houlei.common.codec.ObjectCodec;
import houlei.common.codec.object.AbstractObjectCodec;

import java.nio.ByteBuffer;

import static houlei.common.codec.utils.ByteBufferUtil.checkRemaining;
import static houlei.common.codec.utils.ByteBufferUtil.expandByteBuffer;


public class EnumCodec extends AbstractObjectCodec<Enum<?>> implements ObjectCodec<Enum<?>> {

    @Override
    protected ByteBuffer encodeValue(ByteBuffer buffer, Enum<?> obj) throws CodecException {
        int ordinal = obj.ordinal();
        buffer = expandByteBuffer(buffer, 4);
        buffer.putInt(ordinal);
        return buffer;
    }

    @Override
    protected Enum<?> decodeValue(ByteBuffer buffer) throws CodecException {
        checkRemaining(buffer, 4);
        int ordinal = buffer.getInt();
        return type.getEnumConstants()[ordinal];
    }

}
