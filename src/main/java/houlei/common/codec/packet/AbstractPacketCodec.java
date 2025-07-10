package houlei.common.codec.packet;

import houlei.common.codec.CodecException;
import houlei.common.codec.SequenceIdGenerator;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Objects;

import static houlei.common.codec.utils.ByteBufferUtil.checkRemaining;
import static houlei.common.codec.utils.ByteBufferUtil.expandByteBuffer;


public abstract class AbstractPacketCodec<T> {

    protected ByteOrder byteOrder = ByteOrder.BIG_ENDIAN;
    protected short version = 0x0100;
    protected int commandId;
    protected SequenceIdGenerator sequenceIdGenerator = SequenceIdGenerator.DEFAULT;
    protected Class<T> type;

    public ByteBuffer encode(ByteBuffer buffer, Packet<T> packet) throws CodecException {
        if (Objects.isNull(packet)) {
            throw new IllegalArgumentException("packet is null");
        }
        buffer = expandByteBuffer(buffer, 16);

        short byteOrder = packet.getByteOrder();
        switch (byteOrder) {
            case 0 :
                byteOrder = this.byteOrder == ByteOrder.BIG_ENDIAN ? (short) 0xFEFF : (short) 0xFFFE;
                break;
            case (short) 0xFEFF :
            case (short) 0xFFFE :
                break;
            default:
                throw new CodecException(String.format("Unsupported byte order: 0x%04X", byteOrder));
        }

        short version = packet.getVersion() == 0 ? this.version : packet.getVersion();
        int commandId = packet.getCommandId() == 0 ? this.commandId : packet.getCommandId();
        int sequenceId = packet.getSequenceId() == 0 ? sequenceIdGenerator.generate() : packet.getSequenceId();
        int packetLength = packet.getBodyLength();

        buffer.putShort(byteOrder);
        buffer.putShort(version);
        buffer.putInt(commandId);
        buffer.putInt(sequenceId);
        buffer.putInt(packetLength);
        int bodyIndex = buffer.position();
        buffer = encodeBody(buffer, packet.getBody());
        packetLength = buffer.position() - bodyIndex;
        packet.setBodyLength(packetLength);
        buffer.putInt(bodyIndex-4, packetLength);
        return buffer;
    }

    protected abstract ByteBuffer encodeBody(ByteBuffer buffer, T body);

    public Packet<T> decode(ByteBuffer buffer) throws CodecException {
        checkRemaining(buffer, 16);

        short byteOrder = buffer.getShort();
        switch (byteOrder) {
            case (short) 0xFEFF : buffer.order(ByteOrder.BIG_ENDIAN);break;
            case (short) 0xFFFE : buffer.order(ByteOrder.LITTLE_ENDIAN);break;
            default : throw new CodecException(String.format("Unsupported byte order: 0x%04X", byteOrder));
        }

        short version = buffer.getShort();
        if (version != this.version) {
            throw new CodecException(String.format("Invalid version: 0x%04X", version));
        }

        int commandId = buffer.getInt();
        if (commandId != this.commandId) {
            throw new CodecException(String.format("Packet id mismatch [%#x / %#x]", commandId, this.commandId));
        }

        int sequenceId = buffer.getInt();
        int bodyLength = buffer.getInt();

        Packet<T> packet = new Packet<>();
        packet.setByteOrder(byteOrder);
        packet.setVersion(version);
        packet.setCommandId(commandId);
        packet.setSequenceId(sequenceId);
        packet.setBodyLength(bodyLength);
        T body = decodeBody(buffer);
        packet.setBody(body);

        return packet;
    }

    protected abstract T decodeBody(ByteBuffer buffer);

    public ByteOrder getByteOrder() {
        return byteOrder;
    }

    public void setByteOrder(ByteOrder byteOrder) {
        this.byteOrder = byteOrder;
    }

    public short getVersion() {
        return version;
    }

    public void setVersion(short version) {
        this.version = version;
    }

    public int getCommandId() {
        return commandId;
    }

    public void setCommandId(int commandId) {
        this.commandId = commandId;
    }

    public SequenceIdGenerator getSequenceIdGenerator() {
        return sequenceIdGenerator;
    }

    public void setSequenceIdGenerator(SequenceIdGenerator sequenceIdGenerator) {
        this.sequenceIdGenerator = sequenceIdGenerator;
    }

    public Class<T> getType() {
        return type;
    }

    public void setType(Class<T> type) {
        this.type = type;
    }

}
