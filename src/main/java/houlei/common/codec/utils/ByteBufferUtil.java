package houlei.common.codec.utils;

import houlei.common.codec.CodecException;

import java.nio.ByteBuffer;
import java.util.Arrays;

import static houlei.common.codec.utils.StringUtil.EMPTY_STRING;
import static houlei.common.codec.utils.StringUtil.NEWLINE;

public class ByteBufferUtil {

    private static final char[] DIGITS = new char[]{
            '0', '1', '2', '3', '4', '5', '6', '7',
            '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'
    };
    private static final char[] BYTE2CHAR = new char[256];
    private static final char[] HEXDUMP_TABLE = new char[256 * 4];
    private static final String[] HEX_PADDING = new String[16];
    private static final String[] HEXDUMP_ROW_PREFIXES = new String[65536 >>> 4];
    private static final String[] BYTE2HEX = new String[256];
    private static final String[] BYTE_PADDING = new String[16];
    private static final String[] BYTE2HEX_PAD = new String[256];

    private static final byte[] HEX2B;
    public static final byte[] EMPTY_BYTES = {};

    static {
        // Generate the lookup table that converts a byte into a 2-digit hexadecimal integer.
        for (int i = 0; i < BYTE2HEX_PAD.length; i++) {
            String str = Integer.toHexString(i);
            BYTE2HEX_PAD[i] = i > 0xf ? str : ('0' + str);
        }

        for (int i = 0; i < 256; i ++) {
            HEXDUMP_TABLE[ i << 1     ] = DIGITS[i >>> 4 & 0x0F];
            HEXDUMP_TABLE[(i << 1) + 1] = DIGITS[i       & 0x0F];
        }

        int i;

        // Generate the lookup table for hex dump paddings
        for (i = 0; i < HEX_PADDING.length; i ++) {
            int padding = HEX_PADDING.length - i;
            StringBuilder buf = new StringBuilder(padding * 3);
            for (int j = 0; j < padding; j ++) {
                buf.append("   ");
            }
            HEX_PADDING[i] = buf.toString();
        }

        // Generate the lookup table for the start-offset header in each row (up to 64KiB).
        for (i = 0; i < HEXDUMP_ROW_PREFIXES.length; i ++) {
            StringBuilder buf = new StringBuilder(12);
            buf.append(NEWLINE);
            buf.append(Long.toHexString(i << 4 & 0xFFFFFFFFL | 0x100000000L));
            buf.setCharAt(buf.length() - 9, '|');
            buf.append('|');
            HEXDUMP_ROW_PREFIXES[i] = buf.toString();
        }

        // Generate the lookup table for byte-to-hex-dump conversion
        for (i = 0; i < BYTE2HEX.length; i ++) {
            BYTE2HEX[i] = ' ' + BYTE2HEX_PAD[i & 0xff];
        }

        // Generate the lookup table for byte dump paddings
        for (i = 0; i < BYTE_PADDING.length; i ++) {
            int padding = BYTE_PADDING.length - i;
            StringBuilder buf = new StringBuilder(padding);
            for (int j = 0; j < padding; j ++) {
                buf.append(' ');
            }
            BYTE_PADDING[i] = buf.toString();
        }

        // Generate the lookup table for byte-to-char conversion
        for (i = 0; i < BYTE2CHAR.length; i ++) {
            if (i <= 0x1f || i >= 0x7f) {
                BYTE2CHAR[i] = '.';
            } else {
                BYTE2CHAR[i] = (char) i;
            }
        }

        // Generate the lookup table that converts an hex char into its decimal value:
        // the size of the table is such that the JVM is capable of save any bounds-check
        // if a char type is used as an index.
        HEX2B = new byte[Character.MAX_VALUE + 1];
        Arrays.fill(HEX2B, (byte) -1);
        HEX2B['0'] = 0;
        HEX2B['1'] = 1;
        HEX2B['2'] = 2;
        HEX2B['3'] = 3;
        HEX2B['4'] = 4;
        HEX2B['5'] = 5;
        HEX2B['6'] = 6;
        HEX2B['7'] = 7;
        HEX2B['8'] = 8;
        HEX2B['9'] = 9;
        HEX2B['A'] = 10;
        HEX2B['B'] = 11;
        HEX2B['C'] = 12;
        HEX2B['D'] = 13;
        HEX2B['E'] = 14;
        HEX2B['F'] = 15;
        HEX2B['a'] = 10;
        HEX2B['b'] = 11;
        HEX2B['c'] = 12;
        HEX2B['d'] = 13;
        HEX2B['e'] = 14;
        HEX2B['f'] = 15;

    }

    /**
     * Checks that the given argument is positive or zero. If it is not , throws {@link IllegalArgumentException}.
     * Otherwise, returns the argument.
     */
    public static void checkPositiveOrZero(int i, String name) {
        if (i < 0) {
            throw new IllegalArgumentException(name + " : " + i + " (expected: >= 0)");
        }
    }

    /**
     * Determine if the requested {@code index} and {@code length} will fit within {@code capacity}.
     * @param index The starting index.
     * @param length The length which will be utilized (starting from {@code index}).
     * @param capacity The capacity that {@code index + length} is allowed to be within.
     * @return {@code false} if the requested {@code index} and {@code length} will fit within {@code capacity}.
     * {@code true} if this would result in an index out of bounds exception.
     */
    public static boolean isOutOfBounds(int index, int length, int capacity) {
        return (index | length | capacity | (index + length) | (capacity - (index + length))) < 0;
    }

    public static String hexDump(byte[] array, int fromIndex, int length) {
        checkPositiveOrZero(length, "length");
        if (length == 0) {
            return EMPTY_STRING;
        }

        int endIndex = fromIndex + length;
        char[] buf = new char[length << 1];

        int srcIdx = fromIndex;
        int dstIdx = 0;
        for (; srcIdx < endIndex; srcIdx ++, dstIdx += 2) {
            System.arraycopy(
                    HEXDUMP_TABLE, (array[srcIdx] & 0xFF) << 1,
                    buf, dstIdx, 2);
        }

        return new String(buf);
    }

    public static String hexDump(ByteBuffer buffer, int fromIndex, int length) {
        checkPositiveOrZero(length, "length");
        if (length == 0) {
            return EMPTY_STRING;
        }

        int endIndex = fromIndex + length;
        char[] buf = new char[length << 1];

        int srcIdx = fromIndex;
        int dstIdx = 0;
        for (; srcIdx < endIndex; srcIdx ++, dstIdx += 2) {
            System.arraycopy(
                    // HEXDUMP_TABLE, buffer.getUnsignedByte(srcIdx) << 1,
                    HEXDUMP_TABLE, (buffer.get(srcIdx) & 0xFF) << 1,
                    buf, dstIdx, 2);
        }

        return new String(buf);
    }

    private static void appendHexDumpRowPrefix(StringBuilder dump, int row, int rowStartIndex) {
        if (row < HEXDUMP_ROW_PREFIXES.length) {
            dump.append(HEXDUMP_ROW_PREFIXES[row]);
        } else {
            dump.append(NEWLINE);
            dump.append(Long.toHexString(rowStartIndex & 0xFFFFFFFFL | 0x100000000L));
            dump.setCharAt(dump.length() - 9, '|');
            dump.append('|');
        }
    }

    private static void appendPrettyHexDump(StringBuilder dump, ByteBuffer buf, int offset, int length) {
        if (isOutOfBounds(offset, length, buf.capacity())) {
            throw new IndexOutOfBoundsException(
                    "expected: " + "0 <= offset(" + offset + ") <= offset + length(" + length
                            + ") <= " + "buf.capacity(" + buf.capacity() + ')');
        }
        if (length == 0) {
            return;
        }
        dump.append("         +-------------------------------------------------+").append(NEWLINE)
                .append("         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |").append(NEWLINE)
                .append("+--------+-------------------------------------------------+----------------+");

        final int fullRows = length >>> 4;
        final int remainder = length & 0xF;

        // Dump the rows which have 16 bytes.
        for (int row = 0; row < fullRows; row ++) {
            int rowStartIndex = (row << 4) + offset;

            // Per-row prefix.
            appendHexDumpRowPrefix(dump, row, rowStartIndex);

            // Hex dump
            int rowEndIndex = rowStartIndex + 16;
            for (int j = rowStartIndex; j < rowEndIndex; j ++) {
//                dump.append(BYTE2HEX[buf.getUnsignedByte(j)]);
                dump.append(BYTE2HEX[buf.get(j) & 0xFF]);
            }
            dump.append(" |");

            // ASCII dump
            for (int j = rowStartIndex; j < rowEndIndex; j ++) {
//                dump.append(BYTE2CHAR[buf.getUnsignedByte(j)]);
                dump.append(BYTE2CHAR[buf.get(j) & 0xFF]);
            }
            dump.append('|');
        }

        // Dump the last row which has less than 16 bytes.
        if (remainder != 0) {
            int rowStartIndex = (fullRows << 4) + offset;
            appendHexDumpRowPrefix(dump, fullRows, rowStartIndex);

            // Hex dump
            int rowEndIndex = rowStartIndex + remainder;
            for (int j = rowStartIndex; j < rowEndIndex; j ++) {
//                dump.append(BYTE2HEX[buf.getUnsignedByte(j)]);
                dump.append(BYTE2HEX[buf.get(j) & 0xFF]);
            }
            dump.append(HEX_PADDING[remainder]);
            dump.append(" |");

            // Ascii dump
            for (int j = rowStartIndex; j < rowEndIndex; j ++) {
//                dump.append(BYTE2CHAR[buf.getUnsignedByte(j)]);
                dump.append(BYTE2CHAR[buf.get(j) & 0xFF]);
            }
            dump.append(BYTE_PADDING[remainder]);
            dump.append('|');
        }

        dump.append(NEWLINE)
                .append("+--------+-------------------------------------------------+----------------+");
    }

    public static String prettyHexDump(ByteBuffer buffer, int offset, int length) {
        if (length == 0) {
            return EMPTY_STRING;
        } else {
            int rows = length / 16 + ((length & 15) == 0? 0 : 1) + 4;
            StringBuilder buf = new StringBuilder(rows * 80);
            appendPrettyHexDump(buf, buffer, offset, length);
            return buf.toString();
        }
    }

    public static String prettyHexDump(ByteBuffer buffer, boolean flip) {
        return prettyHexDump(buffer, flip? 0 : buffer.position(), flip ? buffer.position() : buffer.limit());
    }

    public static String hexDump(ByteBuffer buffer, boolean flip) {
        return hexDump(buffer, flip? 0 : buffer.position(), flip ? buffer.position() : buffer.limit());
    }


    /**
     * Decode a 2-digit hex byte from within a string.
     */
    public static byte decodeHexByte(CharSequence s, int pos) {
        int hi = HEX2B[s.charAt(pos)];
        int lo = HEX2B[s.charAt(pos + 1)];
        if (hi == -1 || lo == -1) {
            throw new IllegalArgumentException(String.format(
                    "invalid hex byte '%s' at index %d of '%s'", s.subSequence(pos, pos + 2), pos, s));
        }
        return (byte) ((hi << 4) + lo);
    }

    /**
     * Decodes part of a string with <a href="https://en.wikipedia.org/wiki/Hex_dump">hex dump</a>
     *
     * @param hexDump a {@link CharSequence} which contains the hex dump
     * @param fromIndex start of hex dump in {@code hexDump}
     * @param length hex string length
     */
    public static byte[] decodeHexDump(CharSequence hexDump, int fromIndex, int length) {
        if (length < 0 || (length & 1) != 0) {
            throw new IllegalArgumentException("length: " + length);
        }
        if (length == 0) {
            return EMPTY_BYTES;
        }
        byte[] bytes = new byte[length >>> 1];
        for (int i = 0; i < length; i += 2) {
            bytes[i >>> 1] = decodeHexByte(hexDump, fromIndex + i);
        }
        return bytes;
    }

    /**
     * Decodes a <a href="https://en.wikipedia.org/wiki/Hex_dump">hex dump</a>
     */
    public static byte[] decodeHexDump(CharSequence hexDump) {
        return decodeHexDump(hexDump, 0, hexDump.length());
    }

    public static String hexDump(byte[] array) {
        return hexDump(array, 0, array.length);
    }


    public static ByteBuffer expandByteBuffer(ByteBuffer buffer, int required, int increment) {
        if (buffer.remaining() < required) {
            return ByteBuffer.allocate(buffer.capacity() + increment).put(buffer.flip()).order(buffer.order());
        }
        return buffer;
    }

    public static ByteBuffer expandByteBuffer(ByteBuffer buffer, int required) {
        return expandByteBuffer(buffer, required, required);
    }

    public static void checkRemaining(ByteBuffer buffer, int required) {
        if (buffer.remaining() < required) {
            throw new CodecException("Not enough bytes to read object");
        }
    }

}
