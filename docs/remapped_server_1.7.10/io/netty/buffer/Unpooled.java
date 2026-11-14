package io.netty.buffer;

import io.netty.util.internal.PlatformDependent;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;

public final class Unpooled {

    private static final ByteBufAllocator ALLOC = UnpooledByteBufAllocator.DEFAULT;
    public static final ByteOrder BIG_ENDIAN = ByteOrder.BIG_ENDIAN;
    public static final ByteOrder LITTLE_ENDIAN = ByteOrder.LITTLE_ENDIAN;
    public static final ByteBuf EMPTY_BUFFER = Unpooled.ALLOC.buffer(0, 0);

    public static ByteBuf buffer() {
        return Unpooled.ALLOC.heapBuffer();
    }

    public static ByteBuf directBuffer() {
        return Unpooled.ALLOC.directBuffer();
    }

    public static ByteBuf buffer(int i) {
        return Unpooled.ALLOC.heapBuffer(i);
    }

    public static ByteBuf directBuffer(int i) {
        return Unpooled.ALLOC.directBuffer(i);
    }

    public static ByteBuf buffer(int i, int j) {
        return Unpooled.ALLOC.heapBuffer(i, j);
    }

    public static ByteBuf directBuffer(int i, int j) {
        return Unpooled.ALLOC.directBuffer(i, j);
    }

    public static ByteBuf wrappedBuffer(byte[] abyte) {
        return (ByteBuf) (abyte.length == 0 ? Unpooled.EMPTY_BUFFER : new UnpooledHeapByteBuf(Unpooled.ALLOC, abyte, abyte.length));
    }

    public static ByteBuf wrappedBuffer(byte[] abyte, int i, int j) {
        return j == 0 ? Unpooled.EMPTY_BUFFER : (i == 0 && j == abyte.length ? wrappedBuffer(abyte) : wrappedBuffer(abyte).slice(i, j));
    }

    public static ByteBuf wrappedBuffer(ByteBuffer bytebuffer) {
        return (ByteBuf) (!bytebuffer.hasRemaining() ? Unpooled.EMPTY_BUFFER : (bytebuffer.hasArray() ? wrappedBuffer(bytebuffer.array(), bytebuffer.arrayOffset() + bytebuffer.position(), bytebuffer.remaining()).order(bytebuffer.order()) : (PlatformDependent.hasUnsafe() ? (bytebuffer.isReadOnly() ? (bytebuffer.isDirect() ? new ReadOnlyUnsafeDirectByteBuf(Unpooled.ALLOC, bytebuffer) : new ReadOnlyByteBufferBuf(Unpooled.ALLOC, bytebuffer)) : new UnpooledUnsafeDirectByteBuf(Unpooled.ALLOC, bytebuffer, bytebuffer.remaining())) : (bytebuffer.isReadOnly() ? new ReadOnlyByteBufferBuf(Unpooled.ALLOC, bytebuffer) : new UnpooledDirectByteBuf(Unpooled.ALLOC, bytebuffer, bytebuffer.remaining())))));
    }

    public static ByteBuf wrappedBuffer(ByteBuf bytebuf) {
        return bytebuf.isReadable() ? bytebuf.slice() : Unpooled.EMPTY_BUFFER;
    }

    public static ByteBuf wrappedBuffer(byte[]... abyte) {
        return wrappedBuffer(16, abyte);
    }

    public static ByteBuf wrappedBuffer(ByteBuf... abytebuf) {
        return wrappedBuffer(16, abytebuf);
    }

    public static ByteBuf wrappedBuffer(ByteBuffer... abytebuffer) {
        return wrappedBuffer(16, abytebuffer);
    }

    public static ByteBuf wrappedBuffer(int i, byte[]... abyte) {
        switch (abyte.length) {
            case 0:
                break;
            case 1:
                if (abyte[0].length != 0) {
                    return wrappedBuffer(abyte[0]);
                }
                break;
            default:
                ArrayList arraylist = new ArrayList(abyte.length);
                byte[][] abyte1 = abyte;
                int j = abyte.length;

                for (int k = 0; k < j; ++k) {
                    byte[] abyte2 = abyte1[k];

                    if (abyte2 == null) {
                        break;
                    }

                    if (abyte2.length > 0) {
                        arraylist.add(wrappedBuffer(abyte2));
                    }
                }

                if (!arraylist.isEmpty()) {
                    return new CompositeByteBuf(Unpooled.ALLOC, false, i, arraylist);
                }
        }

        return Unpooled.EMPTY_BUFFER;
    }

    public static ByteBuf wrappedBuffer(int i, ByteBuf... abytebuf) {
        switch (abytebuf.length) {
            case 0:
                break;
            case 1:
                if (abytebuf[0].isReadable()) {
                    return wrappedBuffer(abytebuf[0].order(Unpooled.BIG_ENDIAN));
                }
                break;
            default:
                ByteBuf[] abytebuf1 = abytebuf;
                int j = abytebuf.length;

                for (int k = 0; k < j; ++k) {
                    ByteBuf bytebuf = abytebuf1[k];

                    if (bytebuf.isReadable()) {
                        return new CompositeByteBuf(Unpooled.ALLOC, false, i, abytebuf);
                    }
                }
        }

        return Unpooled.EMPTY_BUFFER;
    }

    public static ByteBuf wrappedBuffer(int i, ByteBuffer... abytebuffer) {
        switch (abytebuffer.length) {
            case 0:
                break;
            case 1:
                if (abytebuffer[0].hasRemaining()) {
                    return wrappedBuffer(abytebuffer[0].order(Unpooled.BIG_ENDIAN));
                }
                break;
            default:
                ArrayList arraylist = new ArrayList(abytebuffer.length);
                ByteBuffer[] abytebuffer1 = abytebuffer;
                int j = abytebuffer.length;

                for (int k = 0; k < j; ++k) {
                    ByteBuffer bytebuffer = abytebuffer1[k];

                    if (bytebuffer == null) {
                        break;
                    }

                    if (bytebuffer.remaining() > 0) {
                        arraylist.add(wrappedBuffer(bytebuffer.order(Unpooled.BIG_ENDIAN)));
                    }
                }

                if (!arraylist.isEmpty()) {
                    return new CompositeByteBuf(Unpooled.ALLOC, false, i, arraylist);
                }
        }

        return Unpooled.EMPTY_BUFFER;
    }

    public static CompositeByteBuf compositeBuffer() {
        return compositeBuffer(16);
    }

    public static CompositeByteBuf compositeBuffer(int i) {
        return new CompositeByteBuf(Unpooled.ALLOC, false, i);
    }

    public static ByteBuf copiedBuffer(byte[] abyte) {
        return abyte.length == 0 ? Unpooled.EMPTY_BUFFER : wrappedBuffer((byte[]) abyte.clone());
    }

    public static ByteBuf copiedBuffer(byte[] abyte, int i, int j) {
        if (j == 0) {
            return Unpooled.EMPTY_BUFFER;
        } else {
            byte[] abyte1 = new byte[j];

            System.arraycopy(abyte, i, abyte1, 0, j);
            return wrappedBuffer(abyte1);
        }
    }

    public static ByteBuf copiedBuffer(ByteBuffer bytebuffer) {
        int i = bytebuffer.remaining();

        if (i == 0) {
            return Unpooled.EMPTY_BUFFER;
        } else {
            byte[] abyte = new byte[i];
            int j = bytebuffer.position();

            try {
                bytebuffer.get(abyte);
            } finally {
                bytebuffer.position(j);
            }

            return wrappedBuffer(abyte).order(bytebuffer.order());
        }
    }

    public static ByteBuf copiedBuffer(ByteBuf bytebuf) {
        return bytebuf.isReadable() ? bytebuf.copy() : Unpooled.EMPTY_BUFFER;
    }

    public static ByteBuf copiedBuffer(byte[]... abyte) {
        switch (abyte.length) {
            case 0:
                return Unpooled.EMPTY_BUFFER;
            case 1:
                if (abyte[0].length == 0) {
                    return Unpooled.EMPTY_BUFFER;
                }

                return copiedBuffer(abyte[0]);
            default:
                int i = 0;
                byte[][] abyte1 = abyte;
                int j = abyte.length;

                byte[] abyte2;
                int k;

                for (k = 0; k < j; ++k) {
                    abyte2 = abyte1[k];
                    if (Integer.MAX_VALUE - i < abyte2.length) {
                        throw new IllegalArgumentException("The total length of the specified arrays is too big.");
                    }

                    i += abyte2.length;
                }

                if (i == 0) {
                    return Unpooled.EMPTY_BUFFER;
                } else {
                    byte[] abyte3 = new byte[i];

                    j = 0;

                    for (k = 0; j < abyte.length; ++j) {
                        abyte2 = abyte[j];
                        System.arraycopy(abyte2, 0, abyte3, k, abyte2.length);
                        k += abyte2.length;
                    }

                    return wrappedBuffer(abyte3);
                }
        }
    }

    public static ByteBuf copiedBuffer(ByteBuf... abytebuf) {
        switch (abytebuf.length) {
            case 0:
                return Unpooled.EMPTY_BUFFER;
            case 1:
                return copiedBuffer(abytebuf[0]);
            default:
                ByteOrder byteorder = null;
                int i = 0;
                ByteBuf[] abytebuf1 = abytebuf;
                int j = abytebuf.length;
                int k = 0;

                ByteBuf bytebuf;
                int l;

                for (; k < j; ++k) {
                    bytebuf = abytebuf1[k];
                    l = bytebuf.readableBytes();
                    if (l > 0) {
                        if (Integer.MAX_VALUE - i < l) {
                            throw new IllegalArgumentException("The total length of the specified buffers is too big.");
                        }

                        i += l;
                        if (byteorder != null) {
                            if (!byteorder.equals(bytebuf.order())) {
                                throw new IllegalArgumentException("inconsistent byte order");
                            }
                        } else {
                            byteorder = bytebuf.order();
                        }
                    }
                }

                if (i == 0) {
                    return Unpooled.EMPTY_BUFFER;
                } else {
                    byte[] abyte = new byte[i];

                    j = 0;

                    for (k = 0; j < abytebuf.length; ++j) {
                        bytebuf = abytebuf[j];
                        l = bytebuf.readableBytes();
                        bytebuf.getBytes(bytebuf.readerIndex(), abyte, k, l);
                        k += l;
                    }

                    return wrappedBuffer(abyte).order(byteorder);
                }
        }
    }

    public static ByteBuf copiedBuffer(ByteBuffer... abytebuffer) {
        switch (abytebuffer.length) {
            case 0:
                return Unpooled.EMPTY_BUFFER;
            case 1:
                return copiedBuffer(abytebuffer[0]);
            default:
                ByteOrder byteorder = null;
                int i = 0;
                ByteBuffer[] abytebuffer1 = abytebuffer;
                int j = abytebuffer.length;
                int k = 0;

                ByteBuffer bytebuffer;
                int l;

                for (; k < j; ++k) {
                    bytebuffer = abytebuffer1[k];
                    l = bytebuffer.remaining();
                    if (l > 0) {
                        if (Integer.MAX_VALUE - i < l) {
                            throw new IllegalArgumentException("The total length of the specified buffers is too big.");
                        }

                        i += l;
                        if (byteorder != null) {
                            if (!byteorder.equals(bytebuffer.order())) {
                                throw new IllegalArgumentException("inconsistent byte order");
                            }
                        } else {
                            byteorder = bytebuffer.order();
                        }
                    }
                }

                if (i == 0) {
                    return Unpooled.EMPTY_BUFFER;
                } else {
                    byte[] abyte = new byte[i];

                    j = 0;

                    for (k = 0; j < abytebuffer.length; ++j) {
                        bytebuffer = abytebuffer[j];
                        l = bytebuffer.remaining();
                        int i1 = bytebuffer.position();

                        bytebuffer.get(abyte, k, l);
                        bytebuffer.position(i1);
                        k += l;
                    }

                    return wrappedBuffer(abyte).order(byteorder);
                }
        }
    }

    public static ByteBuf copiedBuffer(CharSequence charsequence, Charset charset) {
        if (charsequence == null) {
            throw new NullPointerException("string");
        } else {
            return charsequence instanceof CharBuffer ? copiedBuffer((CharBuffer) charsequence, charset) : copiedBuffer(CharBuffer.wrap(charsequence), charset);
        }
    }

    public static ByteBuf copiedBuffer(CharSequence charsequence, int i, int j, Charset charset) {
        if (charsequence == null) {
            throw new NullPointerException("string");
        } else if (j == 0) {
            return Unpooled.EMPTY_BUFFER;
        } else if (charsequence instanceof CharBuffer) {
            CharBuffer charbuffer = (CharBuffer) charsequence;

            if (charbuffer.hasArray()) {
                return copiedBuffer(charbuffer.array(), charbuffer.arrayOffset() + charbuffer.position() + i, j, charset);
            } else {
                charbuffer = charbuffer.slice();
                charbuffer.limit(j);
                charbuffer.position(i);
                return copiedBuffer(charbuffer, charset);
            }
        } else {
            return copiedBuffer(CharBuffer.wrap(charsequence, i, i + j), charset);
        }
    }

    public static ByteBuf copiedBuffer(char[] achar, Charset charset) {
        return copiedBuffer(achar, 0, achar.length, charset);
    }

    public static ByteBuf copiedBuffer(char[] achar, int i, int j, Charset charset) {
        if (achar == null) {
            throw new NullPointerException("array");
        } else {
            return j == 0 ? Unpooled.EMPTY_BUFFER : copiedBuffer(CharBuffer.wrap(achar, i, j), charset);
        }
    }

    private static ByteBuf copiedBuffer(CharBuffer charbuffer, Charset charset) {
        ByteBuffer bytebuffer = ByteBufUtil.encodeString(charbuffer, charset);
        ByteBuf bytebuf = wrappedBuffer(bytebuffer.array());

        bytebuf.writerIndex(bytebuffer.remaining());
        return bytebuf;
    }

    public static ByteBuf unmodifiableBuffer(ByteBuf bytebuf) {
        ByteOrder byteorder = bytebuf.order();

        return (ByteBuf) (byteorder == Unpooled.BIG_ENDIAN ? new ReadOnlyByteBuf(bytebuf) : (new ReadOnlyByteBuf(bytebuf.order(Unpooled.BIG_ENDIAN))).order(Unpooled.LITTLE_ENDIAN));
    }

    public static ByteBuf copyInt(int i) {
        ByteBuf bytebuf = buffer(4);

        bytebuf.writeInt(i);
        return bytebuf;
    }

    public static ByteBuf copyInt(int... aint) {
        if (aint != null && aint.length != 0) {
            ByteBuf bytebuf = buffer(aint.length * 4);
            int[] aint1 = aint;
            int i = aint.length;

            for (int j = 0; j < i; ++j) {
                int k = aint1[j];

                bytebuf.writeInt(k);
            }

            return bytebuf;
        } else {
            return Unpooled.EMPTY_BUFFER;
        }
    }

    public static ByteBuf copyShort(int i) {
        ByteBuf bytebuf = buffer(2);

        bytebuf.writeShort(i);
        return bytebuf;
    }

    public static ByteBuf copyShort(short... ashort) {
        if (ashort != null && ashort.length != 0) {
            ByteBuf bytebuf = buffer(ashort.length * 2);
            short[] ashort1 = ashort;
            int i = ashort.length;

            for (int j = 0; j < i; ++j) {
                short short0 = ashort1[j];

                bytebuf.writeShort(short0);
            }

            return bytebuf;
        } else {
            return Unpooled.EMPTY_BUFFER;
        }
    }

    public static ByteBuf copyShort(int... aint) {
        if (aint != null && aint.length != 0) {
            ByteBuf bytebuf = buffer(aint.length * 2);
            int[] aint1 = aint;
            int i = aint.length;

            for (int j = 0; j < i; ++j) {
                int k = aint1[j];

                bytebuf.writeShort(k);
            }

            return bytebuf;
        } else {
            return Unpooled.EMPTY_BUFFER;
        }
    }

    public static ByteBuf copyMedium(int i) {
        ByteBuf bytebuf = buffer(3);

        bytebuf.writeMedium(i);
        return bytebuf;
    }

    public static ByteBuf copyMedium(int... aint) {
        if (aint != null && aint.length != 0) {
            ByteBuf bytebuf = buffer(aint.length * 3);
            int[] aint1 = aint;
            int i = aint.length;

            for (int j = 0; j < i; ++j) {
                int k = aint1[j];

                bytebuf.writeMedium(k);
            }

            return bytebuf;
        } else {
            return Unpooled.EMPTY_BUFFER;
        }
    }

    public static ByteBuf copyLong(long i) {
        ByteBuf bytebuf = buffer(8);

        bytebuf.writeLong(i);
        return bytebuf;
    }

    public static ByteBuf copyLong(long... along) {
        if (along != null && along.length != 0) {
            ByteBuf bytebuf = buffer(along.length * 8);
            long[] along1 = along;
            int i = along.length;

            for (int j = 0; j < i; ++j) {
                long k = along1[j];

                bytebuf.writeLong(k);
            }

            return bytebuf;
        } else {
            return Unpooled.EMPTY_BUFFER;
        }
    }

    public static ByteBuf copyBoolean(boolean flag) {
        ByteBuf bytebuf = buffer(1);

        bytebuf.writeBoolean(flag);
        return bytebuf;
    }

    public static ByteBuf copyBoolean(boolean... aboolean) {
        if (aboolean != null && aboolean.length != 0) {
            ByteBuf bytebuf = buffer(aboolean.length);
            boolean[] aboolean1 = aboolean;
            int i = aboolean.length;

            for (int j = 0; j < i; ++j) {
                boolean flag = aboolean1[j];

                bytebuf.writeBoolean(flag);
            }

            return bytebuf;
        } else {
            return Unpooled.EMPTY_BUFFER;
        }
    }

    public static ByteBuf copyFloat(float f) {
        ByteBuf bytebuf = buffer(4);

        bytebuf.writeFloat(f);
        return bytebuf;
    }

    public static ByteBuf copyFloat(float... afloat) {
        if (afloat != null && afloat.length != 0) {
            ByteBuf bytebuf = buffer(afloat.length * 4);
            float[] afloat1 = afloat;
            int i = afloat.length;

            for (int j = 0; j < i; ++j) {
                float f = afloat1[j];

                bytebuf.writeFloat(f);
            }

            return bytebuf;
        } else {
            return Unpooled.EMPTY_BUFFER;
        }
    }

    public static ByteBuf copyDouble(double d0) {
        ByteBuf bytebuf = buffer(8);

        bytebuf.writeDouble(d0);
        return bytebuf;
    }

    public static ByteBuf copyDouble(double... adouble) {
        if (adouble != null && adouble.length != 0) {
            ByteBuf bytebuf = buffer(adouble.length * 8);
            double[] adouble1 = adouble;
            int i = adouble.length;

            for (int j = 0; j < i; ++j) {
                double d0 = adouble1[j];

                bytebuf.writeDouble(d0);
            }

            return bytebuf;
        } else {
            return Unpooled.EMPTY_BUFFER;
        }
    }

    public static ByteBuf unreleasableBuffer(ByteBuf bytebuf) {
        return new UnreleasableByteBuf(bytebuf);
    }

    private Unpooled() {}
}
