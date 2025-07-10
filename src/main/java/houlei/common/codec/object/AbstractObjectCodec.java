package houlei.common.codec.object;

import houlei.common.codec.CodecException;
import houlei.common.codec.object.builtIn.ClassIds;

import java.nio.ByteBuffer;
import java.util.Objects;

import static houlei.common.codec.utils.ByteBufferUtil.expandByteBuffer;
import static houlei.common.codec.utils.ByteBufferUtil.checkRemaining;


public abstract class AbstractObjectCodec<T> {

    protected int classId;
    protected Class<T> type;

    public ByteBuffer encode(ByteBuffer buffer, T value) throws CodecException {
        buffer = expandByteBuffer(buffer, 4);
        if (Objects.isNull(value)) {
            return buffer.putInt(ClassIds.OBJECT_NULL);
        }
        return encodeValue(buffer.putInt(classId), value);
    }

    protected abstract ByteBuffer encodeValue(ByteBuffer byteBuffer, T value) throws CodecException;

    public T decode(ByteBuffer buffer) throws CodecException {
        checkRemaining(buffer, 4);
        int classId = buffer.getInt();
        if (classId == ClassIds.OBJECT_NULL) {
            return null;
        }
        if (classId != this.classId) {
            throw new CodecException(String.format("Object class id mismatch [%#x / %#x]", classId, this.classId));
        }
        return decodeValue(buffer);
    }

    protected abstract T decodeValue(ByteBuffer buffer);

    public int getClassId() {
        return classId;
    }

    public void setClassId(int classId) {
        this.classId = classId;
    }

    public Class<T> getType() {
        return type;
    }

    public void setType(Class<T> type) {
        this.type = type;
    }

}
