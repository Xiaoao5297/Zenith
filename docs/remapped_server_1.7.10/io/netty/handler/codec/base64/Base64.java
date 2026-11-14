package io.netty.handler.codec.base64;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public final class Base64 {

    private static final int MAX_LINE_LENGTH = 76;
    private static final byte EQUALS_SIGN = 61;
    private static final byte NEW_LINE = 10;
    private static final byte WHITE_SPACE_ENC = -5;
    private static final byte EQUALS_SIGN_ENC = -1;

    private static byte[] alphabet(Base64Dialect base64dialect) {
        if (base64dialect == null) {
            throw new NullPointerException("dialect");
        } else {
            return base64dialect.alphabet;
        }
    }

    private static byte[] decodabet(Base64Dialect base64dialect) {
        if (base64dialect == null) {
            throw new NullPointerException("dialect");
        } else {
            return base64dialect.decodabet;
        }
    }

    private static boolean breakLines(Base64Dialect base64dialect) {
        if (base64dialect == null) {
            throw new NullPointerException("dialect");
        } else {
            return base64dialect.breakLinesByDefault;
        }
    }

    public static ByteBuf encode(ByteBuf bytebuf) {
        return encode(bytebuf, Base64Dialect.STANDARD);
    }

    public static ByteBuf encode(ByteBuf bytebuf, Base64Dialect base64dialect) {
        return encode(bytebuf, breakLines(base64dialect), base64dialect);
    }

    public static ByteBuf encode(ByteBuf bytebuf, boolean flag) {
        return encode(bytebuf, flag, Base64Dialect.STANDARD);
    }

    public static ByteBuf encode(ByteBuf bytebuf, boolean flag, Base64Dialect base64dialect) {
        if (bytebuf == null) {
            throw new NullPointerException("src");
        } else {
            ByteBuf bytebuf1 = encode(bytebuf, bytebuf.readerIndex(), bytebuf.readableBytes(), flag, base64dialect);

            bytebuf.readerIndex(bytebuf.writerIndex());
            return bytebuf1;
        }
    }

    public static ByteBuf encode(ByteBuf bytebuf, int i, int j) {
        return encode(bytebuf, i, j, Base64Dialect.STANDARD);
    }

    public static ByteBuf encode(ByteBuf bytebuf, int i, int j, Base64Dialect base64dialect) {
        return encode(bytebuf, i, j, breakLines(base64dialect), base64dialect);
    }

    public static ByteBuf encode(ByteBuf bytebuf, int i, int j, boolean flag) {
        return encode(bytebuf, i, j, flag, Base64Dialect.STANDARD);
    }

    public static ByteBuf encode(ByteBuf bytebuf, int i, int j, boolean flag, Base64Dialect base64dialect) {
        if (bytebuf == null) {
            throw new NullPointerException("src");
        } else if (base64dialect == null) {
            throw new NullPointerException("dialect");
        } else {
            int k = j * 4 / 3;
            ByteBuf bytebuf1 = Unpooled.buffer(k + (j % 3 > 0 ? 4 : 0) + (flag ? k / 76 : 0)).order(bytebuf.order());
            int l = 0;
            int i1 = 0;
            int j1 = j - 2;

            for (int k1 = 0; l < j1; i1 += 4) {
                encode3to4(bytebuf, l + i, 3, bytebuf1, i1, base64dialect);
                k1 += 4;
                if (flag && k1 == 76) {
                    bytebuf1.setByte(i1 + 4, 10);
                    ++i1;
                    k1 = 0;
                }

                l += 3;
            }

            if (l < j) {
                encode3to4(bytebuf, l + i, j - l, bytebuf1, i1, base64dialect);
                i1 += 4;
            }

            return bytebuf1.slice(0, i1);
        }
    }

    private static void encode3to4(ByteBuf bytebuf, int i, int j, ByteBuf bytebuf1, int k, Base64Dialect base64dialect) {
        byte[] abyte = alphabet(base64dialect);
        int l = (j > 0 ? bytebuf.getByte(i) << 24 >>> 8 : 0) | (j > 1 ? bytebuf.getByte(i + 1) << 24 >>> 16 : 0) | (j > 2 ? bytebuf.getByte(i + 2) << 24 >>> 24 : 0);

        switch (j) {
            case 1:
                bytebuf1.setByte(k, abyte[l >>> 18]);
                bytebuf1.setByte(k + 1, abyte[l >>> 12 & 63]);
                bytebuf1.setByte(k + 2, 61);
                bytebuf1.setByte(k + 3, 61);
                break;
            case 2:
                bytebuf1.setByte(k, abyte[l >>> 18]);
                bytebuf1.setByte(k + 1, abyte[l >>> 12 & 63]);
                bytebuf1.setByte(k + 2, abyte[l >>> 6 & 63]);
                bytebuf1.setByte(k + 3, 61);
                break;
            case 3:
                bytebuf1.setByte(k, abyte[l >>> 18]);
                bytebuf1.setByte(k + 1, abyte[l >>> 12 & 63]);
                bytebuf1.setByte(k + 2, abyte[l >>> 6 & 63]);
                bytebuf1.setByte(k + 3, abyte[l & 63]);
        }

    }

    public static ByteBuf decode(ByteBuf bytebuf) {
        return decode(bytebuf, Base64Dialect.STANDARD);
    }

    public static ByteBuf decode(ByteBuf bytebuf, Base64Dialect base64dialect) {
        if (bytebuf == null) {
            throw new NullPointerException("src");
        } else {
            ByteBuf bytebuf1 = decode(bytebuf, bytebuf.readerIndex(), bytebuf.readableBytes(), base64dialect);

            bytebuf.readerIndex(bytebuf.writerIndex());
            return bytebuf1;
        }
    }

    public static ByteBuf decode(ByteBuf bytebuf, int i, int j) {
        return decode(bytebuf, i, j, Base64Dialect.STANDARD);
    }

    public static ByteBuf decode(ByteBuf bytebuf, int i, int j, Base64Dialect base64dialect) {
        if (bytebuf == null) {
            throw new NullPointerException("src");
        } else if (base64dialect == null) {
            throw new NullPointerException("dialect");
        } else {
            byte[] abyte = decodabet(base64dialect);
            int k = j * 3 / 4;
            ByteBuf bytebuf1 = bytebuf.alloc().buffer(k).order(bytebuf.order());
            int l = 0;
            byte[] abyte1 = new byte[4];
            int i1 = 0;

            for (int j1 = i; j1 < i + j; ++j1) {
                byte b0 = (byte) (bytebuf.getByte(j1) & 127);
                byte b1 = abyte[b0];

                if (b1 < -5) {
                    throw new IllegalArgumentException("bad Base64 input character at " + j1 + ": " + bytebuf.getUnsignedByte(j1) + " (decimal)");
                }

                if (b1 >= -1) {
                    abyte1[i1++] = b0;
                    if (i1 > 3) {
                        l += decode4to3(abyte1, 0, bytebuf1, l, base64dialect);
                        i1 = 0;
                        if (b0 == 61) {
                            break;
                        }
                    }
                }
            }

            return bytebuf1.slice(0, l);
        }
    }

    private static int decode4to3(byte[] abyte, int i, ByteBuf bytebuf, int j, Base64Dialect base64dialect) {
        byte[] abyte1 = decodabet(base64dialect);
        int k;

        if (abyte[i + 2] == 61) {
            k = (abyte1[abyte[i]] & 255) << 18 | (abyte1[abyte[i + 1]] & 255) << 12;
            bytebuf.setByte(j, (byte) (k >>> 16));
            return 1;
        } else if (abyte[i + 3] == 61) {
            k = (abyte1[abyte[i]] & 255) << 18 | (abyte1[abyte[i + 1]] & 255) << 12 | (abyte1[abyte[i + 2]] & 255) << 6;
            bytebuf.setByte(j, (byte) (k >>> 16));
            bytebuf.setByte(j + 1, (byte) (k >>> 8));
            return 2;
        } else {
            try {
                k = (abyte1[abyte[i]] & 255) << 18 | (abyte1[abyte[i + 1]] & 255) << 12 | (abyte1[abyte[i + 2]] & 255) << 6 | abyte1[abyte[i + 3]] & 255;
            } catch (IndexOutOfBoundsException indexoutofboundsexception) {
                throw new IllegalArgumentException("not encoded in Base64");
            }

            bytebuf.setByte(j, (byte) (k >> 16));
            bytebuf.setByte(j + 1, (byte) (k >> 8));
            bytebuf.setByte(j + 2, (byte) k);
            return 3;
        }
    }

    private Base64() {}
}
