package houlei.common.codec.object.builtIn;

import houlei.common.codec.CodecException;
import houlei.common.codec.ObjectCodec;

import java.nio.ByteBuffer;

import static houlei.common.codec.utils.ByteBufferUtil.expandByteBuffer;
import static houlei.common.codec.utils.ByteBufferUtil.checkRemaining;


public enum Primitive implements ObjectCodec<Object> {
    Boolean(boolean.class, ClassIds.Boolean){
        @Override
        public ByteBuffer encode(ByteBuffer buffer, Object value) throws CodecException {
            buffer = expandByteBuffer(buffer, 1);
            return buffer.put((byte) ((boolean)value?1:0));
        }

        @Override
        public Object decode(ByteBuffer buffer) throws CodecException {
            checkRemaining(buffer, 1);
            return buffer.get() == 1;
        }
    },
    Byte(byte.class, ClassIds.Byte){
        @Override
        public ByteBuffer encode(ByteBuffer buffer, Object value) throws CodecException {
            buffer = expandByteBuffer(buffer, 1);
            return buffer.put((byte)value);
        }

        @Override
        public Object decode(ByteBuffer buffer) throws CodecException {
            checkRemaining(buffer, 1);
            return (byte)buffer.get();
        }
    },
    Char(char.class, ClassIds.Char){
        @Override
        public ByteBuffer encode(ByteBuffer buffer, Object value) throws CodecException {
            buffer = expandByteBuffer(buffer, 2);
            return buffer.putChar((char)value);
        }

        @Override
        public Object decode(ByteBuffer buffer) throws CodecException {
            checkRemaining(buffer, 2);
            return (char)buffer.getChar();
        }
    },
    Short(short.class, ClassIds.Short){
        @Override
        public ByteBuffer encode(ByteBuffer buffer, Object value) throws CodecException {
            buffer = expandByteBuffer(buffer, 2);
            return buffer.putShort((short)value);
        }

        @Override
        public Object decode(ByteBuffer buffer) throws CodecException {
            checkRemaining(buffer, 2);
            return (short)buffer.getShort();
        }
    },
    Int(int.class, ClassIds.Int){
        @Override
        public ByteBuffer encode(ByteBuffer buffer, Object value) throws CodecException {
            buffer = expandByteBuffer(buffer, 4);
            return buffer.putInt((int)value);
        }

        @Override
        public Object decode(ByteBuffer buffer) throws CodecException {
            checkRemaining(buffer, 4);
            return buffer.getInt();
        }
    },
    Float(float.class, ClassIds.Float){
        @Override
        public ByteBuffer encode(ByteBuffer buffer, Object value) throws CodecException {
            buffer = expandByteBuffer(buffer, 4);
            return buffer.putFloat((float)value);
        }

        @Override
        public Object decode(ByteBuffer buffer) throws CodecException {
            checkRemaining(buffer, 4);
            return buffer.getFloat();
        }
    },
    Long(long.class, ClassIds.Long){
        @Override
        public ByteBuffer encode(ByteBuffer buffer, Object value) throws CodecException {
            buffer = expandByteBuffer(buffer, 8);
            return buffer.putLong((long)value);
        }

        @Override
        public Object decode(ByteBuffer buffer) throws CodecException {
            checkRemaining(buffer, 8);
            return buffer.getLong();
        }
    },
    Double(double.class, ClassIds.Double){
        @Override
        public ByteBuffer encode(ByteBuffer buffer, Object value) throws CodecException {
            buffer = expandByteBuffer(buffer, 8);
            return buffer.putDouble((double)value);
        }

        @Override
        public Object decode(ByteBuffer buffer) throws CodecException {
            checkRemaining(buffer, 8);
            return buffer.getDouble();
        }
    };

    private final Class<?> type;
    private final int classId;

    Primitive(Class<?> type, int classId) {
        this.type = type;
        this.classId = classId;
    }

    public Class<?> getType() {
        return type;
    }

    public int getClassId() {
        return classId;
    }

}
