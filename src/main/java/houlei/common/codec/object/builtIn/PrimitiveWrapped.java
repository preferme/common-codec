package houlei.common.codec.object.builtIn;

import houlei.common.codec.CodecException;
import houlei.common.codec.ObjectCodec;

import java.nio.ByteBuffer;
import java.util.Objects;

import static houlei.common.codec.utils.ByteBufferUtil.expandByteBuffer;
import static houlei.common.codec.utils.ByteBufferUtil.checkRemaining;

public enum PrimitiveWrapped implements ObjectCodec<Object> {

    Boolean(java.lang.Boolean.class, ClassIds.Boolean_Wrapped, Primitive.Boolean),
    Byte(java.lang.Byte.class, ClassIds.Byte_Wrapped, Primitive.Byte),
    Char(java.lang.Character.class, ClassIds.Char_Wrapped, Primitive.Char),
    Short(java.lang.Short.class, ClassIds.Short_Wrapped, Primitive.Short),
    Int(java.lang.Integer.class, ClassIds.Int_Wrapped, Primitive.Int),
    Float(java.lang.Float.class, ClassIds.Float_Wrapped, Primitive.Float),
    Long(java.lang.Long.class, ClassIds.Long_Wrapped, Primitive.Long),
    Double(java.lang.Double.class, ClassIds.Double_Wrapped, Primitive.Double);

    private final Class<?> type;
    private final int classId;
    private final Primitive primitive;

    PrimitiveWrapped(Class<?> type, int classId, Primitive primitive) {
        this.type = type;
        this.classId = classId;
        this.primitive = primitive;
    }

    public Class<?> getType() {
        return type;
    }

    public int getClassId() {
        return classId;
    }

    public Primitive getPrimitive() {
        return primitive;
    }

    @Override
    public ByteBuffer encode(ByteBuffer buffer, Object value) throws CodecException {
        buffer = expandByteBuffer(buffer, 4);
        if (Objects.isNull(value)) {
            return buffer.putInt(ClassIds.OBJECT_NULL);
        }
        return primitive.encode(buffer.putInt(classId), value);
    }

    @Override
    public Object decode(ByteBuffer buffer) throws CodecException {
        checkRemaining(buffer, 4);
        int classId = buffer.getInt();
        if (classId == ClassIds.OBJECT_NULL) {
            return null;
        }
        if (classId != this.classId) {
            throw new CodecException(String.format("Object class id mismatch [%#x / %#x]", classId, this.classId));
        }
        return primitive.decode(buffer);
    }

}
