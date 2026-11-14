package org.apache.commons.codec.binary;

public class Base32 extends BaseNCodec {

    private static final int BITS_PER_ENCODED_BYTE = 5;
    private static final int BYTES_PER_ENCODED_BLOCK = 8;
    private static final int BYTES_PER_UNENCODED_BLOCK = 5;
    private static final byte[] CHUNK_SEPARATOR = new byte[]{13, 10};
    private static final byte[] DECODE_TABLE = new byte[]{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 26, 27, 28, 29, 30, 31, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25};
    private static final byte[] ENCODE_TABLE = new byte[]{65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 50, 51, 52, 53, 54, 55};
    private static final byte[] HEX_DECODE_TABLE = new byte[]{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, -1, -1, -1, -1, -1, -1, -1, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32};
    private static final byte[] HEX_ENCODE_TABLE = new byte[]{48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86};
    private static final int MASK_5BITS = 31;
    private final int decodeSize;
    private final byte[] decodeTable;
    private final int encodeSize;
    private final byte[] encodeTable;
    private final byte[] lineSeparator;

    public Base32() {
        this(false);
    }

    public Base32(boolean flag) {
        this(0, (byte[]) null, flag);
    }

    public Base32(int i) {
        this(i, Base32.CHUNK_SEPARATOR);
    }

    public Base32(int i, byte[] abyte) {
        this(i, abyte, false);
    }

    public Base32(int i, byte[] abyte, boolean flag) {
        super(5, 8, i, abyte == null ? 0 : abyte.length);
        if (flag) {
            this.encodeTable = Base32.HEX_ENCODE_TABLE;
            this.decodeTable = Base32.HEX_DECODE_TABLE;
        } else {
            this.encodeTable = Base32.ENCODE_TABLE;
            this.decodeTable = Base32.DECODE_TABLE;
        }

        if (i > 0) {
            if (abyte == null) {
                throw new IllegalArgumentException("lineLength " + i + " > 0, but lineSeparator is null");
            }

            if (this.containsAlphabetOrPad(abyte)) {
                String s = StringUtils.newStringUtf8(abyte);

                throw new IllegalArgumentException("lineSeparator must not contain Base32 characters: [" + s + "]");
            }

            this.encodeSize = 8 + abyte.length;
            this.lineSeparator = new byte[abyte.length];
            System.arraycopy(abyte, 0, this.lineSeparator, 0, abyte.length);
        } else {
            this.encodeSize = 8;
            this.lineSeparator = null;
        }

        this.decodeSize = this.encodeSize - 1;
    }

    @Override
    void decode(byte[] abyte, int i, int j, BaseNCodec.Context basencodec_context) {
        if (!basencodec_context.eof) {
            if (j < 0) {
                basencodec_context.eof = true;
            }

            for (int k = 0; k < j; ++k) {
                byte b0 = abyte[i++];

                if (b0 == 61) {
                    basencodec_context.eof = true;
                    break;
                }

                byte[] abyte1 = this.ensureBufferSize(this.decodeSize, basencodec_context);

                if (b0 >= 0 && b0 < this.decodeTable.length) {
                    byte b1 = this.decodeTable[b0];

                    if (b1 >= 0) {
                        basencodec_context.modulus = (basencodec_context.modulus + 1) % 8;
                        basencodec_context.lbitWorkArea = (basencodec_context.lbitWorkArea << 5) + (long) b1;
                        if (basencodec_context.modulus == 0) {
                            abyte1[basencodec_context.pos++] = (byte) ((int) (basencodec_context.lbitWorkArea >> 32 & 255L));
                            abyte1[basencodec_context.pos++] = (byte) ((int) (basencodec_context.lbitWorkArea >> 24 & 255L));
                            abyte1[basencodec_context.pos++] = (byte) ((int) (basencodec_context.lbitWorkArea >> 16 & 255L));
                            abyte1[basencodec_context.pos++] = (byte) ((int) (basencodec_context.lbitWorkArea >> 8 & 255L));
                            abyte1[basencodec_context.pos++] = (byte) ((int) (basencodec_context.lbitWorkArea & 255L));
                        }
                    }
                }
            }

            if (basencodec_context.eof && basencodec_context.modulus >= 2) {
                byte[] abyte2 = this.ensureBufferSize(this.decodeSize, basencodec_context);

                switch (basencodec_context.modulus) {
                    case 2:
                        abyte2[basencodec_context.pos++] = (byte) ((int) (basencodec_context.lbitWorkArea >> 2 & 255L));
                        break;
                    case 3:
                        abyte2[basencodec_context.pos++] = (byte) ((int) (basencodec_context.lbitWorkArea >> 7 & 255L));
                        break;
                    case 4:
                        basencodec_context.lbitWorkArea >>= 4;
                        abyte2[basencodec_context.pos++] = (byte) ((int) (basencodec_context.lbitWorkArea >> 8 & 255L));
                        abyte2[basencodec_context.pos++] = (byte) ((int) (basencodec_context.lbitWorkArea & 255L));
                        break;
                    case 5:
                        basencodec_context.lbitWorkArea >>= 1;
                        abyte2[basencodec_context.pos++] = (byte) ((int) (basencodec_context.lbitWorkArea >> 16 & 255L));
                        abyte2[basencodec_context.pos++] = (byte) ((int) (basencodec_context.lbitWorkArea >> 8 & 255L));
                        abyte2[basencodec_context.pos++] = (byte) ((int) (basencodec_context.lbitWorkArea & 255L));
                        break;
                    case 6:
                        basencodec_context.lbitWorkArea >>= 6;
                        abyte2[basencodec_context.pos++] = (byte) ((int) (basencodec_context.lbitWorkArea >> 16 & 255L));
                        abyte2[basencodec_context.pos++] = (byte) ((int) (basencodec_context.lbitWorkArea >> 8 & 255L));
                        abyte2[basencodec_context.pos++] = (byte) ((int) (basencodec_context.lbitWorkArea & 255L));
                        break;
                    case 7:
                        basencodec_context.lbitWorkArea >>= 3;
                        abyte2[basencodec_context.pos++] = (byte) ((int) (basencodec_context.lbitWorkArea >> 24 & 255L));
                        abyte2[basencodec_context.pos++] = (byte) ((int) (basencodec_context.lbitWorkArea >> 16 & 255L));
                        abyte2[basencodec_context.pos++] = (byte) ((int) (basencodec_context.lbitWorkArea >> 8 & 255L));
                        abyte2[basencodec_context.pos++] = (byte) ((int) (basencodec_context.lbitWorkArea & 255L));
                        break;
                    default:
                        throw new IllegalStateException("Impossible modulus " + basencodec_context.modulus);
                }
            }

        }
    }

    @Override
    void encode(byte[] abyte, int i, int j, BaseNCodec.Context basencodec_context) {
        if (!basencodec_context.eof) {
            if (j < 0) {
                basencodec_context.eof = true;
                if (0 == basencodec_context.modulus && this.lineLength == 0) {
                    return;
                }

                byte[] abyte1 = this.ensureBufferSize(this.encodeSize, basencodec_context);
                int k = basencodec_context.pos;

                switch (basencodec_context.modulus) {
                    case 0:
                        break;
                    case 1:
                        abyte1[basencodec_context.pos++] = this.encodeTable[(int) (basencodec_context.lbitWorkArea >> 3) & 31];
                        abyte1[basencodec_context.pos++] = this.encodeTable[(int) (basencodec_context.lbitWorkArea << 2) & 31];
                        abyte1[basencodec_context.pos++] = 61;
                        abyte1[basencodec_context.pos++] = 61;
                        abyte1[basencodec_context.pos++] = 61;
                        abyte1[basencodec_context.pos++] = 61;
                        abyte1[basencodec_context.pos++] = 61;
                        abyte1[basencodec_context.pos++] = 61;
                        break;
                    case 2:
                        abyte1[basencodec_context.pos++] = this.encodeTable[(int) (basencodec_context.lbitWorkArea >> 11) & 31];
                        abyte1[basencodec_context.pos++] = this.encodeTable[(int) (basencodec_context.lbitWorkArea >> 6) & 31];
                        abyte1[basencodec_context.pos++] = this.encodeTable[(int) (basencodec_context.lbitWorkArea >> 1) & 31];
                        abyte1[basencodec_context.pos++] = this.encodeTable[(int) (basencodec_context.lbitWorkArea << 4) & 31];
                        abyte1[basencodec_context.pos++] = 61;
                        abyte1[basencodec_context.pos++] = 61;
                        abyte1[basencodec_context.pos++] = 61;
                        abyte1[basencodec_context.pos++] = 61;
                        break;
                    case 3:
                        abyte1[basencodec_context.pos++] = this.encodeTable[(int) (basencodec_context.lbitWorkArea >> 19) & 31];
                        abyte1[basencodec_context.pos++] = this.encodeTable[(int) (basencodec_context.lbitWorkArea >> 14) & 31];
                        abyte1[basencodec_context.pos++] = this.encodeTable[(int) (basencodec_context.lbitWorkArea >> 9) & 31];
                        abyte1[basencodec_context.pos++] = this.encodeTable[(int) (basencodec_context.lbitWorkArea >> 4) & 31];
                        abyte1[basencodec_context.pos++] = this.encodeTable[(int) (basencodec_context.lbitWorkArea << 1) & 31];
                        abyte1[basencodec_context.pos++] = 61;
                        abyte1[basencodec_context.pos++] = 61;
                        abyte1[basencodec_context.pos++] = 61;
                        break;
                    case 4:
                        abyte1[basencodec_context.pos++] = this.encodeTable[(int) (basencodec_context.lbitWorkArea >> 27) & 31];
                        abyte1[basencodec_context.pos++] = this.encodeTable[(int) (basencodec_context.lbitWorkArea >> 22) & 31];
                        abyte1[basencodec_context.pos++] = this.encodeTable[(int) (basencodec_context.lbitWorkArea >> 17) & 31];
                        abyte1[basencodec_context.pos++] = this.encodeTable[(int) (basencodec_context.lbitWorkArea >> 12) & 31];
                        abyte1[basencodec_context.pos++] = this.encodeTable[(int) (basencodec_context.lbitWorkArea >> 7) & 31];
                        abyte1[basencodec_context.pos++] = this.encodeTable[(int) (basencodec_context.lbitWorkArea >> 2) & 31];
                        abyte1[basencodec_context.pos++] = this.encodeTable[(int) (basencodec_context.lbitWorkArea << 3) & 31];
                        abyte1[basencodec_context.pos++] = 61;
                        break;
                    default:
                        throw new IllegalStateException("Impossible modulus " + basencodec_context.modulus);
                }

                basencodec_context.currentLinePos += basencodec_context.pos - k;
                if (this.lineLength > 0 && basencodec_context.currentLinePos > 0) {
                    System.arraycopy(this.lineSeparator, 0, abyte1, basencodec_context.pos, this.lineSeparator.length);
                    basencodec_context.pos += this.lineSeparator.length;
                }
            } else {
                for (int l = 0; l < j; ++l) {
                    byte[] abyte2 = this.ensureBufferSize(this.encodeSize, basencodec_context);

                    basencodec_context.modulus = (basencodec_context.modulus + 1) % 5;
                    int i1 = abyte[i++];

                    if (i1 < 0) {
                        i1 += 256;
                    }

                    basencodec_context.lbitWorkArea = (basencodec_context.lbitWorkArea << 8) + (long) i1;
                    if (0 == basencodec_context.modulus) {
                        abyte2[basencodec_context.pos++] = this.encodeTable[(int) (basencodec_context.lbitWorkArea >> 35) & 31];
                        abyte2[basencodec_context.pos++] = this.encodeTable[(int) (basencodec_context.lbitWorkArea >> 30) & 31];
                        abyte2[basencodec_context.pos++] = this.encodeTable[(int) (basencodec_context.lbitWorkArea >> 25) & 31];
                        abyte2[basencodec_context.pos++] = this.encodeTable[(int) (basencodec_context.lbitWorkArea >> 20) & 31];
                        abyte2[basencodec_context.pos++] = this.encodeTable[(int) (basencodec_context.lbitWorkArea >> 15) & 31];
                        abyte2[basencodec_context.pos++] = this.encodeTable[(int) (basencodec_context.lbitWorkArea >> 10) & 31];
                        abyte2[basencodec_context.pos++] = this.encodeTable[(int) (basencodec_context.lbitWorkArea >> 5) & 31];
                        abyte2[basencodec_context.pos++] = this.encodeTable[(int) basencodec_context.lbitWorkArea & 31];
                        basencodec_context.currentLinePos += 8;
                        if (this.lineLength > 0 && this.lineLength <= basencodec_context.currentLinePos) {
                            System.arraycopy(this.lineSeparator, 0, abyte2, basencodec_context.pos, this.lineSeparator.length);
                            basencodec_context.pos += this.lineSeparator.length;
                            basencodec_context.currentLinePos = 0;
                        }
                    }
                }
            }

        }
    }

    @Override
    public boolean isInAlphabet(byte b0) {
        return b0 >= 0 && b0 < this.decodeTable.length && this.decodeTable[b0] != -1;
    }
}
