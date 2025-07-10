package houlei.common.codec.packet;

public class Packet<T> {

    private short byteOrder;
    private short version;
    private int commandId;
    private int sequenceId;
    private int bodyLength;
    private T body;

    @Override
    public String toString() {
        return "Packet{" +
                String.format("byteOrder=0x%04X", byteOrder) +
                String.format(",version=0x%04X", version) +
                String.format(",commandId=0x%08X", commandId) +
                String.format(",sequenceId=0x%08X(%d)", sequenceId, sequenceId) +
                String.format(",bodyLength=0x%08X(%d)", bodyLength, bodyLength) +
                ", body=" + body +
                '}';
    }

    public short getByteOrder() {
        return byteOrder;
    }

    public void setByteOrder(short byteOrder) {
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

    public int getSequenceId() {
        return sequenceId;
    }

    public void setSequenceId(int sequenceId) {
        this.sequenceId = sequenceId;
    }

    public int getBodyLength() {
        return bodyLength;
    }

    public void setBodyLength(int bodyLength) {
        this.bodyLength = bodyLength;
    }

    public T getBody() {
        return body;
    }

    public void setBody(T body) {
        this.body = body;
    }

}
