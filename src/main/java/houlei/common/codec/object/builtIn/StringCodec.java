package houlei.common.codec.object.builtIn;

import houlei.common.codec.CodecException;
import houlei.common.codec.ObjectCodec;
import houlei.common.codec.object.AbstractObjectCodec;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import static houlei.common.codec.utils.ByteBufferUtil.expandByteBuffer;
import static houlei.common.codec.utils.ByteBufferUtil.checkRemaining;


public class StringCodec extends AbstractObjectCodec<String> implements ObjectCodec<String> {

    private Charset encoding = StandardCharsets.UTF_8;

    @Override
    protected ByteBuffer encodeValue(ByteBuffer buffer, String obj) throws CodecException {
        byte[] value = obj.getBytes(encoding);
        int length = value.length;

        buffer = expandByteBuffer(buffer, length + 4);

        buffer.putInt(length);
        buffer.put(value);

        return buffer;
    }

    @Override
    protected String decodeValue(ByteBuffer buffer) throws CodecException {
        checkRemaining(buffer, 4);
        int length = buffer.getInt();

        checkRemaining(buffer, length);
        byte[] value = new byte[length];
        buffer.get(value);

        return new String(value, encoding);
    }

    public Charset getEncoding() {
        return encoding;
    }

    public void setEncoding(Charset encoding) {
        this.encoding = encoding;
    }

}
