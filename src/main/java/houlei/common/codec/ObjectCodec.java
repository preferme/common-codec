package houlei.common.codec;

import java.nio.ByteBuffer;

public interface ObjectCodec<T> {

    ByteBuffer encode(ByteBuffer buffer, T value) throws CodecException;

    T decode(ByteBuffer buffer) throws CodecException;

}
