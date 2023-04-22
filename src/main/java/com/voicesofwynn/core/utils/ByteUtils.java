package com.voicesofwynn.core.utils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 * Utils to handle Byte type
 */
public class ByteUtils {


    public static byte[] encodeInteger(int integer) {
        return new byte[] {
                (byte)(integer >>> 24),
                (byte)(integer >>> 16),
                (byte)(integer >>> 8),
                (byte)integer
        };
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static int readInteger(InputStream input) throws IOException {
        byte[] b = new byte[4];
        input.read(b);
        return ByteBuffer.wrap(b).getInt();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static float readFloat(InputStream input) throws IOException {
        byte[] b = new byte[4];
        input.read(b);
        return ByteBuffer.wrap(b).getFloat();
    }

    public static byte[] encodeFloat(float f) {
        return ByteBuffer.allocate(4).putFloat(f).array();
    }

    public static byte[] encodeLong(long l) {
        return ByteBuffer.allocate(8).putLong(l).array();
    }

    public static byte[] encodeString (String str) {

        byte[] sb = str.getBytes();
        byte[] b = new byte[4+sb.length];
        System.arraycopy(encodeInteger(sb.length), 0, b, 0, 4);
        System.arraycopy(sb, 0, b, 4, sb.length);

        return b;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static String readString(InputStream input) throws IOException {
        int size = readInteger(input);
        byte[] st = new byte[size];
        input.read(st);

        return new String(st);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static byte readByte(InputStream input) throws IOException {
        byte[] st = new byte[1];
        input.read(st);
        return st[0];
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static long readLong(InputStream input) throws IOException {
        byte[] b = new byte[8];
        input.read(b);
        return ByteBuffer.wrap(b).getLong();
    }

}
