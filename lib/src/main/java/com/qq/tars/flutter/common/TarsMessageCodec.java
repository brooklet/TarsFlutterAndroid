package com.qq.tars.flutter.common;

import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.flutter.BuildConfig;
import io.flutter.Log;
import io.flutter.plugin.common.MessageCodec;

public class TarsMessageCodec implements MessageCodec<Object> {
    public static final String TAG = "TarsMessageCodec#";
    public static final TarsMessageCodec INSTANCE = new TarsMessageCodec();

    @Override
    public ByteBuffer encodeMessage(Object message) {
        if (message == null) {
            return null;
        }
        final TarsMessageCodec.ExposedByteArrayOutputStream stream = new TarsMessageCodec.ExposedByteArrayOutputStream();
        writeValue(stream, message);
        final ByteBuffer buffer = ByteBuffer.allocateDirect(stream.size());
        buffer.put(stream.buffer(), 0, stream.size());
        return buffer;
    }

    @Override
    public Object decodeMessage(ByteBuffer message) {
        if (message == null) {
            return null;
        }
        message.order(ByteOrder.nativeOrder());
        final Object value = readValue(message);
        if (message.hasRemaining()) {
            throw new IllegalArgumentException("Message corrupted");
        }
        return value;
    }

    public static final boolean LITTLE_ENDIAN = ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN;
    public static final Charset UTF8 = Charset.forName("UTF8");
    public static final byte NULL = 0;
    public static final byte TRUE = 1;
    public static final byte FALSE = 2;
    public static final byte INT = 3;
    public static final byte LONG = 4;
    public static final byte BIGINT = 5;
    public static final byte DOUBLE = 6;
    public static final byte STRING = 7;
    public static final byte BYTE_ARRAY = 8;
    public static final byte INT_ARRAY = 9;
    public static final byte LONG_ARRAY = 10;
    public static final byte DOUBLE_ARRAY = 11;
    public static final byte LIST = 12;
    public static final byte MAP = 13;
//    public static final byte TARS = 20;

    /**
     * Writes an int representing a size to the specified stream. Uses an expanding code of 1 to 5
     * bytes to optimize for small values.
     */
    public static final void writeSize(ByteArrayOutputStream stream, int value) {
        if (BuildConfig.DEBUG && 0 > value) {
            Log.e(TAG, "Attempted to write a negative size.");
        }
        if (value < 254) {
            stream.write(value);
        } else if (value <= 0xffff) {
            stream.write(254);
            writeChar(stream, value);
        } else {
            stream.write(255);
            writeInt(stream, value);
        }
    }

    /** Writes the least significant two bytes of the specified int to the specified stream. */
    public static final void writeChar(ByteArrayOutputStream stream, int value) {
        if (LITTLE_ENDIAN) {
            stream.write(value);
            stream.write(value >>> 8);
        } else {
            stream.write(value >>> 8);
            stream.write(value);
        }
    }

    /** Writes the specified int as 4 bytes to the specified stream. */
    public static final void writeInt(ByteArrayOutputStream stream, int value) {
        if (LITTLE_ENDIAN) {
            stream.write(value);
            stream.write(value >>> 8);
            stream.write(value >>> 16);
            stream.write(value >>> 24);
        } else {
            stream.write(value >>> 24);
            stream.write(value >>> 16);
            stream.write(value >>> 8);
            stream.write(value);
        }
    }

    /** Writes the specified long as 8 bytes to the specified stream. */
    public static final void writeLong(ByteArrayOutputStream stream, long value) {
        if (LITTLE_ENDIAN) {
            stream.write((byte) value);
            stream.write((byte) (value >>> 8));
            stream.write((byte) (value >>> 16));
            stream.write((byte) (value >>> 24));
            stream.write((byte) (value >>> 32));
            stream.write((byte) (value >>> 40));
            stream.write((byte) (value >>> 48));
            stream.write((byte) (value >>> 56));
        } else {
            stream.write((byte) (value >>> 56));
            stream.write((byte) (value >>> 48));
            stream.write((byte) (value >>> 40));
            stream.write((byte) (value >>> 32));
            stream.write((byte) (value >>> 24));
            stream.write((byte) (value >>> 16));
            stream.write((byte) (value >>> 8));
            stream.write((byte) value);
        }
    }

    /** Writes the specified double as 8 bytes to the specified stream. */
    public static final void writeDouble(ByteArrayOutputStream stream, double value) {
        writeLong(stream, Double.doubleToLongBits(value));
    }

    /** Writes the length and then the actual bytes of the specified array to the specified stream. */
    public static final void writeBytes(ByteArrayOutputStream stream, byte[] bytes) {
        writeSize(stream, bytes.length);
        stream.write(bytes, 0, bytes.length);
    }

    /**
     * Writes a number of padding bytes to the specified stream to ensure that the next value is
     * aligned to a whole multiple of the specified alignment. An example usage with alignment = 8 is
     * to ensure doubles are word-aligned in the stream.
     */
    public static final void writeAlignment(ByteArrayOutputStream stream, int alignment) {
        final int mod = stream.size() % alignment;
        if (mod != 0) {
            for (int i = 0; i < alignment - mod; i++) {
                stream.write(0);
            }
        }
    }

    /**
     * Writes a type discriminator byte and then a byte serialization of the specified value to the
     * specified stream.
     *
     * <p>Subclasses can extend the codec by overriding this method, calling super for values that the
     * extension does not handle.
     */
    public void writeValue(ByteArrayOutputStream stream, Object value) {
        if (value == null || value.equals(null)) {
            stream.write(NULL);
        } else if (value instanceof Boolean) {
            stream.write(((Boolean) value).booleanValue() ? TRUE : FALSE);
        } else if (value instanceof Number) {
            if (value instanceof Integer || value instanceof Short || value instanceof Byte) {
                stream.write(INT);
                writeInt(stream, ((Number) value).intValue());
            } else if (value instanceof Long) {
                stream.write(LONG);
                writeLong(stream, (long) value);
            } else if (value instanceof Float || value instanceof Double) {
                stream.write(DOUBLE);
                writeAlignment(stream, 8);
                writeDouble(stream, ((Number) value).doubleValue());
            } else if (value instanceof BigInteger) {
                stream.write(BIGINT);
                writeBytes(stream, ((BigInteger) value).toString(16).getBytes(UTF8));
            } else {
                throw new IllegalArgumentException("Unsupported Number type: " + value.getClass());
            }
        } else if (value instanceof String) {
            stream.write(STRING);
            writeBytes(stream, ((String) value).getBytes(UTF8));
        } else if (value instanceof byte[]) {
            stream.write(BYTE_ARRAY);
            writeBytes(stream, (byte[]) value);
        } else if (value instanceof int[]) {
            stream.write(INT_ARRAY);
            final int[] array = (int[]) value;
            writeSize(stream, array.length);
            writeAlignment(stream, 4);
            for (final int n : array) {
                writeInt(stream, n);
            }
        } else if (value instanceof long[]) {
            stream.write(LONG_ARRAY);
            final long[] array = (long[]) value;
            writeSize(stream, array.length);
            writeAlignment(stream, 8);
            for (final long n : array) {
                writeLong(stream, n);
            }
        } else if (value instanceof double[]) {
            stream.write(DOUBLE_ARRAY);
            final double[] array = (double[]) value;
            writeSize(stream, array.length);
            writeAlignment(stream, 8);
            for (final double d : array) {
                writeDouble(stream, d);
            }
        } else if (value instanceof List) {
            stream.write(LIST);
            final List<?> list = (List) value;
            writeSize(stream, list.size());
            for (final Object o : list) {
                writeValue(stream, o);
            }
        } else if (value instanceof Map) {
            stream.write(MAP);
            final Map<?, ?> map = (Map) value;
            writeSize(stream, map.size());
            for (final Map.Entry<?, ?> entry : map.entrySet()) {
                writeValue(stream, entry.getKey());
                writeValue(stream, entry.getValue());
            }
        } else {
            throw new IllegalArgumentException("Unsupported value: " + value);
        }
    }

    /** Reads an int representing a size as written by writeSize. */
    public static final int readSize(ByteBuffer buffer) {
        if (!buffer.hasRemaining()) {
            throw new IllegalArgumentException("Message corrupted");
        }
        final int value = buffer.get() & 0xff;
        if (value < 254) {
            return value;
        } else if (value == 254) {
            return buffer.getChar();
        } else {
            return buffer.getInt();
        }
    }

    /** Reads a byte array as written by writeBytes. */
    public static final byte[] readBytes(ByteBuffer buffer) {
        final int length = readSize(buffer);
        final byte[] bytes = new byte[length];
        buffer.get(bytes);
        return bytes;
    }

    /** Reads alignment padding bytes as written by writeAlignment. */
    public static final void readAlignment(ByteBuffer buffer, int alignment) {
        final int mod = buffer.position() % alignment;
        if (mod != 0) {
            buffer.position(buffer.position() + alignment - mod);
        }
    }

    /** Reads a value as written by writeValue. */
    public final Object readValue(ByteBuffer buffer) {
        if (!buffer.hasRemaining()) {
            throw new IllegalArgumentException("Message corrupted");
        }
        final byte type = buffer.get();
        return readValueOfType(type, buffer);
    }

    /**
     * Reads a value of the specified type.
     *
     * <p>Subclasses may extend the codec by overriding this method, calling super for types that the
     * extension does not handle.
     */
    public Object readValueOfType(byte type, ByteBuffer buffer) {
        final Object result;
        switch (type) {
            case NULL:
                result = null;
                break;
            case TRUE:
                result = true;
                break;
            case FALSE:
                result = false;
                break;
            case INT:
                result = buffer.getInt();
                break;
            case LONG:
                result = buffer.getLong();
                break;
            case BIGINT:
            {
                final byte[] hex = readBytes(buffer);
                result = new BigInteger(new String(hex, UTF8), 16);
                break;
            }
            case DOUBLE:
                readAlignment(buffer, 8);
                result = buffer.getDouble();
                break;
            case STRING:
            {
                final byte[] bytes = readBytes(buffer);
                result = new String(bytes, UTF8);
                break;
            }
            case BYTE_ARRAY:
            {
                result = readBytes(buffer);
                break;
            }
            case INT_ARRAY:
            {
                final int length = readSize(buffer);
                final int[] array = new int[length];
                readAlignment(buffer, 4);
                buffer.asIntBuffer().get(array);
                result = array;
                buffer.position(buffer.position() + 4 * length);
                break;
            }
            case LONG_ARRAY:
            {
                final int length = readSize(buffer);
                final long[] array = new long[length];
                readAlignment(buffer, 8);
                buffer.asLongBuffer().get(array);
                result = array;
                buffer.position(buffer.position() + 8 * length);
                break;
            }
            case DOUBLE_ARRAY:
            {
                final int length = readSize(buffer);
                final double[] array = new double[length];
                readAlignment(buffer, 8);
                buffer.asDoubleBuffer().get(array);
                result = array;
                buffer.position(buffer.position() + 8 * length);
                break;
            }
            case LIST:
            {
                final int size = readSize(buffer);
                final List<Object> list = new ArrayList<>(size);
                for (int i = 0; i < size; i++) {
                    list.add(readValue(buffer));
                }
                result = list;
                break;
            }
            case MAP:
            {
                final int size = readSize(buffer);
                final Map<Object, Object> map = new HashMap<>();
                for (int i = 0; i < size; i++) {
                    map.put(readValue(buffer), readValue(buffer));
                }
                result = map;
                break;
            }
            default:
                throw new IllegalArgumentException("Message corrupted");
        }
        return result;
    }

    public static final class ExposedByteArrayOutputStream extends ByteArrayOutputStream {
        byte[] buffer() {
            return buf;
        }
    }
}