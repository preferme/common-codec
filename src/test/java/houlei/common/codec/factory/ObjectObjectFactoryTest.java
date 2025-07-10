package houlei.common.codec.factory;

import houlei.common.codec.ObjectCodec;
import houlei.common.codec.annotation.Object;
import houlei.common.codec.annotation.FieldList;
import houlei.common.codec.utils.ByteBufferUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ObjectCodecFactoryTest {

    enum Color{
        RED, GREEN, BLUE
    }

    @Object(classId = 0x12345678)
    public static class TargetClass{
        private final int int_field = 0x66554321;
        int[] array_field = {1, 2, 3};
        Color[] array_enum = {Color.RED, Color.GREEN, Color.BLUE, null};
        @FieldList(elementType = Integer.class)
        List<Integer> list_field = Arrays.asList(0x10, 0x20, 0x30);

        @Override
        public String toString() {
            return "TargetClass{" +
                    "int_field=0x" + Integer.toHexString(int_field) +
                    ", array_field=" + Arrays.toString(array_field) +
                    ", array_enum=" + Arrays.toString(array_enum) +
                    ", list_field=" + list_field +
                    '}';
        }
    }

    static TargetClass target = new TargetClass();

    @BeforeEach
    void setUp() {
    }

    @Test
    void create() {
        ObjectCodecFactory factory = new ObjectCodecFactory();
        ObjectCodec<TargetClass> codec = factory.create(TargetClass.class);
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        codec.encode(buffer, target);
        System.out.println(ByteBufferUtil.prettyHexDump(buffer, true));
        buffer.flip();
        TargetClass target2 = codec.decode(buffer);
        System.out.println(target2);
        assertEquals(target.int_field, target2.int_field);
    }

}