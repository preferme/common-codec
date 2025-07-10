package houlei.common.codec.factory;

import houlei.common.codec.PacketCodec;
import houlei.common.codec.annotation.Packet;
import houlei.common.codec.utils.ByteBufferUtil;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;

import static org.junit.jupiter.api.Assertions.*;

class PacketObjectFactoryTest {

    @Packet(commandId = 0x12345678)
    static class TestClass {

        @Override
        public String toString() {
            return "TestClass{}";
        }
    }

    private final ObjectCodecFactory objectCodecFactory = new ObjectCodecFactory();
    private final PacketCodecFactory packetCodecFactory = new PacketCodecFactory(objectCodecFactory);

    @Test
    void create() {
        TestClass testClass = new TestClass();

        houlei.common.codec.packet.Packet<TestClass> target = new houlei.common.codec.packet.Packet<>();
        target.setBody(testClass);

        PacketCodec<TestClass> codec = packetCodecFactory.create(TestClass.class);

        ByteBuffer buffer = ByteBuffer.allocate(1024);
        codec.encode(buffer, target);
        System.out.println(ByteBufferUtil.prettyHexDump(buffer, true));
        buffer.flip();
        houlei.common.codec.packet.Packet<TestClass> target2 = codec.decode(buffer);
        System.out.println(target2);

    }

}