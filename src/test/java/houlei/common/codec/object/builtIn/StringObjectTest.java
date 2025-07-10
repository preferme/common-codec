package houlei.common.codec.object.builtIn;

import houlei.common.codec.utils.ByteBufferUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;

import static org.junit.jupiter.api.Assertions.*;

class StringObjectTest {

    final String Value = "中国人民";
    final String Binary = "010203040000000ce4b8ade59bbde4babae6b091";
    final StringCodec codec = new StringCodec();

    @BeforeEach
    void setUp() {
        codec.setType(String.class);
        codec.setClassId(0x01020304);
    }

    @Test
    void encodeValue() {
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        buffer = codec.encode(buffer, Value);
        System.out.println(ByteBufferUtil.prettyHexDump(buffer, true));
    }

    @Test
    void decodeValue() {
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        byte[] binary = ByteBufferUtil.decodeHexDump(Binary);
        buffer.put(binary);
        buffer.flip();
        String value = codec.decode(buffer);
        System.out.println(value);
        assertEquals(Value, value);
    }

}