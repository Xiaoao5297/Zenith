package io.netty.buffer;

import io.netty.util.CharsetUtil;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;

public final class ByteBufUtil {

    private static final char[] HEXDUMP_TABLE = new char[1024];

    public static String hexDump(ByteBuf bytebuf) {
        return hexDump(bytebuf, bytebuf.readerIndex(), bytebuf.readableBytes());
    }

    public static String hexDump(ByteBuf bytebuf, int i, int j) {
        if (j < 0) {
            throw new IllegalArgumentException("length: " + j);
        } else if (j == 0) {
            return "";
        } else {
            int k = i + j;
            char[] achar = new char[j << 1];
            int l = i;

            for (int i1 = 0; l < k; i1 += 2) {
                System.arraycopy(ByteBufUtil.HEXDUMP_TABLE, bytebuf.getUnsignedByte(l) << 1, achar, i1, 2);
                ++l;
            }

            return new String(achar);
        }
    }

    public static int hashCode(ByteBuf bytebuf) {
        int i = bytebuf.readableBytes();
        int j = i >>> 2;
        int k = i & 3;
        int l = 1;
        int i1 = bytebuf.readerIndex();
        int j1;

        if (bytebuf.order() == ByteOrder.BIG_ENDIAN) {
            for (j1 = j; j1 > 0; --j1) {
                l = 31 * l + bytebuf.getInt(i1);
                i1 += 4;
            }
        } else {
            for (j1 = j; j1 > 0; --j1) {
                l = 31 * l + swapInt(bytebuf.getInt(i1));
                i1 += 4;
            }
        }

        for (j1 = k; j1 > 0; --j1) {
            l = 31 * l + bytebuf.getByte(i1++);
        }

        if (l == 0) {
            l = 1;
        }

        return l;
    }

    public static boolean equals(ByteBuf bytebuf, ByteBuf bytebuf1) {
        int i = bytebuf.readableBytes();

        if (i != bytebuf1.readableBytes()) {
            return false;
        } else {
            int j = i >>> 3;
            int k = i & 7;
            int l = bytebuf.readerIndex();
            int i1 = bytebuf1.readerIndex();
            int j1;

            if (bytebuf.order() == bytebuf1.order()) {
                for (j1 = j; j1 > 0; --j1) {
                    if (bytebuf.getLong(l) != bytebuf1.getLong(i1)) {
                        return false;
                    }

                    l += 8;
                    i1 += 8;
                }
            } else {
                for (j1 = j; j1 > 0; --j1) {
                    if (bytebuf.getLong(l) != swapLong(bytebuf1.getLong(i1))) {
                        return false;
                    }

                    l += 8;
                    i1 += 8;
                }
            }

            for (j1 = k; j1 > 0; --j1) {
                if (bytebuf.getByte(l) != bytebuf1.getByte(i1)) {
                    return false;
                }

                ++l;
                ++i1;
            }

            return true;
        }
    }

    public static int compare(ByteBuf bytebuf, ByteBuf bytebuf1) {
        int i = bytebuf.readableBytes();
        int j = bytebuf1.readableBytes();
        int k = Math.min(i, j);
        int l = k >>> 2;
        int i1 = k & 3;
        int j1 = bytebuf.readerIndex();
        int k1 = bytebuf1.readerIndex();
        long l1;
        long i2;
        int j2;

        if (bytebuf.order() == bytebuf1.order()) {
            for (j2 = l; j2 > 0; --j2) {
                l1 = bytebuf.getUnsignedInt(j1);
                i2 = bytebuf1.getUnsignedInt(k1);
                if (l1 > i2) {
                    return 1;
                }

                if (l1 < i2) {
                    return -1;
                }

                j1 += 4;
                k1 += 4;
            }
        } else {
            for (j2 = l; j2 > 0; --j2) {
                l1 = bytebuf.getUnsignedInt(j1);
                i2 = (long) swapInt(bytebuf1.getInt(k1)) & 4294967295L;
                if (l1 > i2) {
                    return 1;
                }

                if (l1 < i2) {
                    return -1;
                }

                j1 += 4;
                k1 += 4;
            }
        }

        for (j2 = i1; j2 > 0; --j2) {
            short short0 = bytebuf.getUnsignedByte(j1);
            short short1 = bytebuf1.getUnsignedByte(k1);

            if (short0 > short1) {
                return 1;
            }

            if (short0 < short1) {
                return -1;
            }

            ++j1;
            ++k1;
        }

        return i - j;
    }

    public static int indexOf(ByteBuf bytebuf, int i, int j, byte b0) {
        return i <= j ? firstIndexOf(bytebuf, i, j, b0) : lastIndexOf(bytebuf, i, j, b0);
    }

    public static short swapShort(short short0) {
        return Short.reverseBytes(short0);
    }

    public static int swapMedium(int i) {
        int j = i << 16 & 16711680 | i & '\uff00' | i >>> 16 & 255;

        if ((j & 8388608) != 0) {
            j |= -16777216;
        }

        return j;
    }

    public static int swapInt(int i) {
        return Integer.reverseBytes(i);
    }

    public static long swapLong(long i) {
        return Long.reverseBytes(i);
    }

    public static ByteBuf readBytes(ByteBufAllocator bytebufallocator, ByteBuf bytebuf, int i) {
        boolean flag = true;
        ByteBuf bytebuf1 = bytebufallocator.buffer(i);

        ByteBuf bytebuf2;

        try {
            bytebuf.readBytes(bytebuf1);
            flag = false;
            bytebuf2 = bytebuf1;
        } finally {
            if (flag) {
                bytebuf1.release();
            }

        }

        return bytebuf2;
    }

    private static int firstIndexOf(ByteBuf bytebuf, int i, int j, byte b0) {
        i = Math.max(i, 0);
        if (i < j && bytebuf.capacity() != 0) {
            for (int k = i; k < j; ++k) {
                if (bytebuf.getByte(k) == b0) {
                    return k;
                }
            }

            return -1;
        } else {
            return -1;
        }
    }

    private static int lastIndexOf(ByteBuf bytebuf, int i, int j, byte b0) {
        i = Math.min(i, bytebuf.capacity());
        if (i >= 0 && bytebuf.capacity() != 0) {
            for (int k = i - 1; k >= j; --k) {
                if (bytebuf.getByte(k) == b0) {
                    return k;
                }
            }

            return -1;
        } else {
            return -1;
        }
    }

    static ByteBuffer encodeString(CharBuffer charbuffer, Charset charset) {
        CharsetEncoder charsetencoder = CharsetUtil.getEncoder(charset);
        ByteBuffer bytebuffer = ByteBuffer.allocate((int) ((double) charbuffer.remaining() * (double) charsetencoder.maxBytesPerChar()));

        try {
            CoderResult coderresult = charsetencoder.encode(charbuffer, bytebuffer, true);

            if (!coderresult.isUnderflow()) {
                coderresult.throwException();
            }

            coderresult = charsetencoder.flush(bytebuffer);
            if (!coderresult.isUnderflow()) {
                coderresult.throwException();
            }
        } catch (CharacterCodingException charactercodingexception) {
            throw new IllegalStateException(charactercodingexception);
        }

        bytebuffer.flip();
        return bytebuffer;
    }

    static String decodeString(ByteBuffer bytebuffer, Charset charset) {
        CharsetDecoder charsetdecoder = CharsetUtil.getDecoder(charset);
        CharBuffer charbuffer = CharBuffer.allocate((int) ((double) bytebuffer.remaining() * (double) charsetdecoder.maxCharsPerByte()));

        try {
            CoderResult coderresult = charsetdecoder.decode(bytebuffer, charbuffer, true);

            if (!coderresult.isUnderflow()) {
                coderresult.throwException();
            }

            coderresult = charsetdecoder.flush(charbuffer);
            if (!coderresult.isUnderflow()) {
                coderresult.throwException();
            }
        } catch (CharacterCodingException charactercodingexception) {
            throw new IllegalStateException(charactercodingexception);
        }

        return charbuffer.flip().toString();
    }

    private ByteBufUtil() {}

    static {
        char[] achar = "0123456789abcdef".toCharArray();

        for (int i = 0; i < 256; ++i) {
            ByteBufUtil.HEXDUMP_TABLE[i << 1] = achar[i >>> 4 & 15];
            ByteBufUtil.HEXDUMP_TABLE[(i << 1) + 1] = achar[i & 15];
        }

    }
}
