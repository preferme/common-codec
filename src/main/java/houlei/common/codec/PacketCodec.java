package houlei.common.codec;

import houlei.common.codec.packet.Packet;

import java.nio.ByteBuffer;


public interface PacketCodec<T> {

    ByteBuffer encode(ByteBuffer buffer, Packet<T> packet) throws CodecException;

    Packet<T> decode(ByteBuffer data) throws CodecException;

}
