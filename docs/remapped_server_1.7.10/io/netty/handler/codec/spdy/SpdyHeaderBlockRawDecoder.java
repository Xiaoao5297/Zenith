package io.netty.handler.codec.spdy;

import io.netty.buffer.ByteBuf;

public class SpdyHeaderBlockRawDecoder extends SpdyHeaderBlockDecoder {

    private final int version;
    private final int maxHeaderSize;
    private final int lengthFieldSize;
    private int headerSize;
    private int numHeaders;

    public SpdyHeaderBlockRawDecoder(int i, int j) {
        if (i >= 2 && i <= 3) {
            this.version = i;
            this.maxHeaderSize = j;
            this.lengthFieldSize = i < 3 ? 2 : 4;
            this.reset();
        } else {
            throw new IllegalArgumentException("unsupported version: " + i);
        }
    }

    private int readLengthField(ByteBuf bytebuf) {
        int i;

        if (this.version < 3) {
            i = SpdyCodecUtil.getUnsignedShort(bytebuf, bytebuf.readerIndex());
            bytebuf.skipBytes(2);
        } else {
            i = SpdyCodecUtil.getSignedInt(bytebuf, bytebuf.readerIndex());
            bytebuf.skipBytes(4);
        }

        return i;
    }

    @Override
    void decode(ByteBuf bytebuf, SpdyHeadersFrame spdyheadersframe) throws Exception {
        if (bytebuf == null) {
            throw new NullPointerException("encoded");
        } else if (spdyheadersframe == null) {
            throw new NullPointerException("frame");
        } else {
            if (this.numHeaders == -1) {
                if (bytebuf.readableBytes() < this.lengthFieldSize) {
                    return;
                }

                this.numHeaders = this.readLengthField(bytebuf);
                if (this.numHeaders < 0) {
                    spdyheadersframe.setInvalid();
                    return;
                }
            }

            while (this.numHeaders > 0) {
                int i = this.headerSize;

                bytebuf.markReaderIndex();
                if (bytebuf.readableBytes() < this.lengthFieldSize) {
                    bytebuf.resetReaderIndex();
                    return;
                }

                int j = this.readLengthField(bytebuf);

                if (j <= 0) {
                    spdyheadersframe.setInvalid();
                    return;
                }

                i += j;
                if (i > this.maxHeaderSize) {
                    spdyheadersframe.setTruncated();
                    return;
                }

                if (bytebuf.readableBytes() < j) {
                    bytebuf.resetReaderIndex();
                    return;
                }

                byte[] abyte = new byte[j];

                bytebuf.readBytes(abyte);
                String s = new String(abyte, "UTF-8");

                if (spdyheadersframe.headers().contains(s)) {
                    spdyheadersframe.setInvalid();
                    return;
                }

                if (bytebuf.readableBytes() < this.lengthFieldSize) {
                    bytebuf.resetReaderIndex();
                    return;
                }

                int k = this.readLengthField(bytebuf);

                if (k < 0) {
                    spdyheadersframe.setInvalid();
                    return;
                }

                if (k == 0) {
                    if (this.version < 3) {
                        spdyheadersframe.setInvalid();
                        return;
                    }

                    spdyheadersframe.headers().add(s, (Object) "");
                    --this.numHeaders;
                    this.headerSize = i;
                } else {
                    i += k;
                    if (i > this.maxHeaderSize) {
                        spdyheadersframe.setTruncated();
                        return;
                    }

                    if (bytebuf.readableBytes() < k) {
                        bytebuf.resetReaderIndex();
                        return;
                    }

                    byte[] abyte1 = new byte[k];

                    bytebuf.readBytes(abyte1);
                    int l = 0;

                    for (int i1 = 0; l < k; i1 = l) {
                        while (l < abyte1.length && abyte1[l] != 0) {
                            ++l;
                        }

                        if (l < abyte1.length && abyte1[l + 1] == 0) {
                            spdyheadersframe.setInvalid();
                            return;
                        }

                        String s1 = new String(abyte1, i1, l - i1, "UTF-8");

                        try {
                            spdyheadersframe.headers().add(s, (Object) s1);
                        } catch (IllegalArgumentException illegalargumentexception) {
                            spdyheadersframe.setInvalid();
                            return;
                        }

                        ++l;
                    }

                    --this.numHeaders;
                    this.headerSize = i;
                }
            }

        }
    }

    @Override
    void reset() {
        this.headerSize = 0;
        this.numHeaders = -1;
    }

    @Override
    void end() {}
}
