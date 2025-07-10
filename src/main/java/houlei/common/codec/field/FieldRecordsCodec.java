package houlei.common.codec.field;

import houlei.common.codec.CodecException;
import houlei.common.codec.ObjectCodec;
import houlei.common.codec.field.action.SetAction;

import java.nio.ByteBuffer;
import java.util.List;

public class FieldRecordsCodec<T> {

    protected List<FieldRecord<T, ?>> fieldRecords;

    @SuppressWarnings("unchecked")
    public ByteBuffer encodeFields(ByteBuffer buffer, T obj) throws CodecException {

        for (FieldRecord<T, ?> fieldRecord : fieldRecords) {
            Object value = fieldRecord.getAction().execute(obj);
            ObjectCodec<Object> codec = (ObjectCodec<Object>) fieldRecord.fieldCodec();
            buffer = codec.encode(buffer, value);
        }

        return buffer;
    }

    @SuppressWarnings("unchecked")
    public void decodeFields(ByteBuffer buffer, T obj) throws CodecException {

        for (FieldRecord<T, ?> fieldRecord : fieldRecords) {
            Object value = fieldRecord.fieldCodec().decode(buffer);
            SetAction<T, Object> setAction = (SetAction<T, Object>) fieldRecord.setAction();
            setAction.execute(obj, value);
        }

    }

    public void setFieldRecords(List<FieldRecord<T, ?>> fieldRecords) {
        this.fieldRecords = fieldRecords;
    }

}
