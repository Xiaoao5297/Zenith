package io.netty.handler.codec.compression;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import java.util.zip.CRC32;

public class Snappy {

    private static final int MAX_HT_SIZE = 16384;
    private static final int MIN_COMPRESSIBLE_BYTES = 15;
    private static final int PREAMBLE_NOT_FULL = -1;
    private static final int NOT_ENOUGH_INPUT = -1;
    private static final int LITERAL = 0;
    private static final int COPY_1_BYTE_OFFSET = 1;
    private static final int COPY_2_BYTE_OFFSET = 2;
    private static final int COPY_4_BYTE_OFFSET = 3;
    private Snappy.State state;
    private byte tag;
    private int written;

    public Snappy() {
        this.state = Snappy.State.READY;
    }

    public void reset() {
        this.state = Snappy.State.READY;
        this.tag = 0;
        this.written = 0;
    }

    public void encode(ByteBuf bytebuf, ByteBuf bytebuf1, int i) {
        int j = 0;

        while (true) {
            int k = i >>> j * 7;

            if ((k & Byte.MIN_VALUE) == 0) {
                bytebuf1.writeByte(k);
                j = bytebuf.readerIndex();
                k = bytebuf.readerIndex();
                int l = i;
                short[] ashort = getHashTable(i);
                int i1 = 32 - (int) Math.floor(Math.log((double) ashort.length) / Math.log(2.0D));
                int j1 = j;

                if (i - j >= 15) {
                    ++j;
                    int k1 = hash(bytebuf, j, i1);

                    label36:
                    while (true) {
                        int l1 = 32;
                        int i2 = j;

                        int j2;
                        int k2;
                        int l2;

                        do {
                            j = i2;
                            j2 = k1;
                            k2 = l1++ >> 5;
                            i2 += k2;
                            if (i2 > l - 4) {
                                break label36;
                            }

                            k1 = hash(bytebuf, i2, i1);
                            l2 = k + ashort[j2];
                            ashort[j2] = (short) (j - k);
                        } while (bytebuf.getInt(j) != bytebuf.getInt(l2));

                        encodeLiteral(bytebuf, bytebuf1, j - j1);

                        do {
                            k2 = j;
                            int i3 = 4 + findMatchingLength(bytebuf, l2 + 4, j + 4, l);

                            j += i3;
                            int j3 = k2 - l2;

                            encodeCopy(bytebuf1, j3, i3);
                            bytebuf.readerIndex(bytebuf.readerIndex() + i3);
                            j2 = j - 1;
                            j1 = j;
                            if (j >= l - 4) {
                                break label36;
                            }

                            int k3 = hash(bytebuf, j2, i1);

                            ashort[k3] = (short) (j - k - 1);
                            int l3 = hash(bytebuf, j2 + 1, i1);

                            l2 = k + ashort[l3];
                            ashort[l3] = (short) (j - k);
                        } while (bytebuf.getInt(j2 + 1) == bytebuf.getInt(l2));

                        k1 = hash(bytebuf, j2 + 2, i1);
                        ++j;
                    }
                }

                if (j1 < l) {
                    encodeLiteral(bytebuf, bytebuf1, l - j1);
                }

                return;
            }

            bytebuf1.writeByte(k & 127 | 128);
            ++j;
        }
    }

    private static int hash(ByteBuf bytebuf, int i, int j) {
        return bytebuf.getInt(i) + 506832829 >>> j;
    }

    private static short[] getHashTable(int i) {
        int j;

        for (j = 256; j < 16384 && j < i; j <<= 1) {
            ;
        }

        short[] ashort;

        if (j <= 256) {
            ashort = new short[256];
        } else {
            ashort = new short[16384];
        }

        return ashort;
    }

    private static int findMatchingLength(ByteBuf bytebuf, int i, int j, int k) {
        int l;

        for (l = 0; j <= k - 4 && bytebuf.getInt(j) == bytebuf.getInt(i + l); l += 4) {
            j += 4;
        }

        while (j < k && bytebuf.getByte(i + l) == bytebuf.getByte(j)) {
            ++j;
            ++l;
        }

        return l;
    }

    private static int bitsToEncode(int i) {
        int j = Integer.highestOneBit(i);

        int k;

        for (k = 0; (j >>= 1) != 0; ++k) {
            ;
        }

        return k;
    }

    private static void encodeLiteral(ByteBuf bytebuf, ByteBuf bytebuf1, int i) {
        if (i < 61) {
            bytebuf1.writeByte(i - 1 << 2);
        } else {
            int j = bitsToEncode(i - 1);
            int k = 1 + j / 8;

            bytebuf1.writeByte(59 + k << 2);

            for (int l = 0; l < k; ++l) {
                bytebuf1.writeByte(i - 1 >> l * 8 & 255);
            }
        }

        bytebuf1.writeBytes(bytebuf, i);
    }

    private static void encodeCopyWithOffset(ByteBuf bytebuf, int i, int j) {
        if (j < 12 && i < 2048) {
            bytebuf.writeByte(1 | j - 4 << 2 | i >> 8 << 5);
            bytebuf.writeByte(i & 255);
        } else {
            bytebuf.writeByte(2 | j - 1 << 2);
            bytebuf.writeByte(i & 255);
            bytebuf.writeByte(i >> 8 & 255);
        }

    }

    private static void encodeCopy(ByteBuf bytebuf, int i, int j) {
        while (j >= 68) {
            encodeCopyWithOffset(bytebuf, i, 64);
            j -= 64;
        }

        if (j > 64) {
            encodeCopyWithOffset(bytebuf, i, 60);
            j -= 60;
        }

        encodeCopyWithOffset(bytebuf, i, j);
    }

    public void decode(ByteBuf bytebuf, ByteBuf bytebuf1) {
        while (bytebuf.isReadable()) {
            switch (this.state) {
                case READY:
                    this.state = Snappy.State.READING_PREAMBLE;
                case READING_PREAMBLE:
                    int i = readPreamble(bytebuf);

                    if (i == -1) {
                        return;
                    }

                    if (i == 0) {
                        this.state = Snappy.State.READY;
                        return;
                    }

                    bytebuf1.ensureWritable(i);
                    this.state = Snappy.State.READING_TAG;
                case READING_TAG:
                    if (!bytebuf.isReadable()) {
                        return;
                    }

                    this.tag = bytebuf.readByte();
                    switch (this.tag & 3) {
                        case 0:
                            this.state = Snappy.State.READING_LITERAL;
                            continue;
                        case 1:
                        case 2:
                        case 3:
                            this.state = Snappy.State.READING_COPY;
                        default:
                            continue;
                    }
                case READING_LITERAL:
                    int j = decodeLiteral(this.tag, bytebuf, bytebuf1);

                    if (j != -1) {
                        this.state = Snappy.State.READING_TAG;
                        this.written += j;
                        break;
                    }

                    return;
                case READING_COPY:
                    int k;

                    switch (this.tag & 3) {
                        case 1:
                            k = decodeCopyWith1ByteOffset(this.tag, bytebuf, bytebuf1, this.written);
                            if (k != -1) {
                                this.state = Snappy.State.READING_TAG;
                                this.written += k;
                                break;
                            }

                            return;
                        case 2:
                            k = decodeCopyWith2ByteOffset(this.tag, bytebuf, bytebuf1, this.written);
                            if (k != -1) {
                                this.state = Snappy.State.READING_TAG;
                                this.written += k;
                                break;
                            }

                            return;
                        case 3:
                            k = decodeCopyWith4ByteOffset(this.tag, bytebuf, bytebuf1, this.written);
                            if (k == -1) {
                                return;
                            }

                            this.state = Snappy.State.READING_TAG;
                            this.written += k;
                    }
            }
        }

    }

    private static int readPreamble(ByteBuf bytebuf) {
        int i = 0;
        int j = 0;

        do {
            if (!bytebuf.isReadable()) {
                return 0;
            }

            short short0 = bytebuf.readUnsignedByte();

            i |= (short0 & 127) << j++ * 7;
            if ((short0 & 128) == 0) {
                return i;
            }
        } while (j < 4);

        throw new DecompressionException("Preamble is greater than 4 bytes");
    }

    private static int decodeLiteral(byte b0, ByteBuf bytebuf, ByteBuf bytebuf1) {
        bytebuf.markReaderIndex();
        int i;

        switch (b0 >> 2 & 63) {
            case 60:
                if (!bytebuf.isReadable()) {
                    return -1;
                }

                i = bytebuf.readUnsignedByte();
                break;
            case 61:
                if (bytebuf.readableBytes() < 2) {
                    return -1;
                }

                i = ByteBufUtil.swapShort(bytebuf.readShort());
                break;
            case 62:
                if (bytebuf.readableBytes() < 3) {
                    return -1;
                }

                i = ByteBufUtil.swapMedium(bytebuf.readUnsignedMedium());
                break;
            case 63:
            default:
                i = b0 >> 2 & 63;
                break;
            case 64:
                if (bytebuf.readableBytes() < 4) {
                    return -1;
                }

                i = ByteBufUtil.swapInt(bytebuf.readInt());
        }

        ++i;
        if (bytebuf.readableBytes() < i) {
            bytebuf.resetReaderIndex();
            return -1;
        } else {
            bytebuf1.writeBytes(bytebuf, i);
            return i;
        }
    }

    private static int decodeCopyWith1ByteOffset(byte b0, ByteBuf bytebuf, ByteBuf bytebuf1, int i) {
        if (!bytebuf.isReadable()) {
            return -1;
        } else {
            int j = bytebuf1.writerIndex();
            int k = 4 + ((b0 & 28) >> 2);
            int l = (b0 & 224) << 8 >> 5 | bytebuf.readUnsignedByte();

            validateOffset(l, i);
            bytebuf1.markReaderIndex();
            if (l < k) {
                for (int i1 = k / l; i1 > 0; --i1) {
                    bytebuf1.readerIndex(j - l);
                    bytebuf1.readBytes(bytebuf1, l);
                }

                if (k % l != 0) {
                    bytebuf1.readerIndex(j - l);
                    bytebuf1.readBytes(bytebuf1, k % l);
                }
            } else {
                bytebuf1.readerIndex(j - l);
                bytebuf1.readBytes(bytebuf1, k);
            }

            bytebuf1.resetReaderIndex();
            return k;
        }
    }

    private static int decodeCopyWith2ByteOffset(byte b0, ByteBuf bytebuf, ByteBuf bytebuf1, int i) {
        if (bytebuf.readableBytes() < 2) {
            return -1;
        } else {
            int j = bytebuf1.writerIndex();
            int k = 1 + (b0 >> 2 & 63);
            short short0 = ByteBufUtil.swapShort(bytebuf.readShort());

            validateOffset(short0, i);
            bytebuf1.markReaderIndex();
            if (short0 < k) {
                for (int l = k / short0; l > 0; --l) {
                    bytebuf1.readerIndex(j - short0);
                    bytebuf1.readBytes(bytebuf1, short0);
                }

                if (k % short0 != 0) {
                    bytebuf1.readerIndex(j - short0);
                    bytebuf1.readBytes(bytebuf1, k % short0);
                }
            } else {
                bytebuf1.readerIndex(j - short0);
                bytebuf1.readBytes(bytebuf1, k);
            }

            bytebuf1.resetReaderIndex();
            return k;
        }
    }

    private static int decodeCopyWith4ByteOffset(byte b0, ByteBuf bytebuf, ByteBuf bytebuf1, int i) {
        if (bytebuf.readableBytes() < 4) {
            return -1;
        } else {
            int j = bytebuf1.writerIndex();
            int k = 1 + (b0 >> 2 & 63);
            int l = ByteBufUtil.swapInt(bytebuf.readInt());

            validateOffset(l, i);
            bytebuf1.markReaderIndex();
            if (l < k) {
                for (int i1 = k / l; i1 > 0; --i1) {
                    bytebuf1.readerIndex(j - l);
                    bytebuf1.readBytes(bytebuf1, l);
                }

                if (k % l != 0) {
                    bytebuf1.readerIndex(j - l);
                    bytebuf1.readBytes(bytebuf1, k % l);
                }
            } else {
                bytebuf1.readerIndex(j - l);
                bytebuf1.readBytes(bytebuf1, k);
            }

            bytebuf1.resetReaderIndex();
            return k;
        }
    }

    private static void validateOffset(int i, int j) {
        if (i > 32767) {
            throw new DecompressionException("Offset exceeds maximum permissible value");
        } else if (i <= 0) {
            throw new DecompressionException("Offset is less than minimum permissible value");
        } else if (i > j) {
            throw new DecompressionException("Offset exceeds size of chunk");
        }
    }

    public static int calculateChecksum(ByteBuf bytebuf) {
        return calculateChecksum(bytebuf, bytebuf.readerIndex(), bytebuf.readableBytes());
    }

    public static int calculateChecksum(ByteBuf bytebuf, int i, int j) {
        CRC32 crc32 = new CRC32();

        int k;

        try {
            if (bytebuf.hasArray()) {
                crc32.update(bytebuf.array(), bytebuf.arrayOffset() + i, j);
            } else {
                byte[] abyte = new byte[j];

                bytebuf.getBytes(i, abyte);
                crc32.update(abyte);
            }

            k = maskChecksum((int) crc32.getValue());
        } finally {
            crc32.reset();
        }

        return k;
    }

    static void validateChecksum(int i, ByteBuf bytebuf) {
        validateChecksum(i, bytebuf, bytebuf.readerIndex(), bytebuf.readableBytes());
    }

    static void validateChecksum(int i, ByteBuf bytebuf, int j, int k) {
        int l = calculateChecksum(bytebuf, j, k);

        if (l != i) {
            throw new DecompressionException("mismatching checksum: " + Integer.toHexString(l) + " (expected: " + Integer.toHexString(i) + ')');
        }
    }

    static int maskChecksum(int i) {
        return (i >> 15 | i << 17) + -1568478504;
    }

    private static enum State {

        READY, READING_PREAMBLE, READING_TAG, READING_LITERAL, READING_COPY;

        private State() {}
    }
}
