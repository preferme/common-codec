package houlei.common.codec.factory;

import houlei.common.codec.PacketCodec;
import houlei.common.codec.SequenceIdGenerator;
import houlei.common.codec.annotation.Packet;
import houlei.common.codec.field.FieldRecordsCodec;
import houlei.common.codec.packet.DefaultPacketCodec;

import java.nio.ByteOrder;

public class PacketCodecFactory {

    private ObjectFactory objectFactory = ObjectFactory.DEFAULT;
    private ObjectCodecFactory objectCodecFactory;

    public PacketCodecFactory(ObjectCodecFactory objectCodecFactory, ObjectFactory objectFactory) {
        this.objectCodecFactory = objectCodecFactory;
        this.objectFactory = objectFactory;
    }

    public PacketCodecFactory(ObjectCodecFactory objectCodecFactory) {
        this.objectCodecFactory = objectCodecFactory;
    }

    public <T> PacketCodec<T> create(Class<T> objectType) {
        // 1 parse @Packet
        Packet annoPacket = objectType.getAnnotation(Packet.class);
        if (annoPacket == null) {
            throw new IllegalArgumentException(objectType.getName() + " is not annotated with @Packet");
        }

        int commandId = annoPacket.commandId();
        if (commandId == 0) {
            throw new IllegalArgumentException("commandId in @Packet must be a non-zero integer");
        }

        // 2 parse fields
        FieldRecordsCodec<T> fieldRecordsCodec = objectCodecFactory.createFieldRecordsCodec(objectType);

        DefaultPacketCodec<T> codec = new DefaultPacketCodec<T>();
        codec.setByteOrder(ByteOrder.BIG_ENDIAN);
        codec.setVersion((short)0x10);
        codec.setCommandId(commandId);
        codec.setSequenceIdGenerator(SequenceIdGenerator.DEFAULT);
        codec.setType(objectType);
        codec.setObjectFactory(objectFactory);
        codec.setFieldRecordsCodec(fieldRecordsCodec);

        return codec;
    }

    public void setObjectFactory(ObjectFactory objectFactory) {
        this.objectFactory = objectFactory;
    }

    public void setObjectCodecFactory(ObjectCodecFactory objectCodecFactory) {
        this.objectCodecFactory = objectCodecFactory;
    }

}
