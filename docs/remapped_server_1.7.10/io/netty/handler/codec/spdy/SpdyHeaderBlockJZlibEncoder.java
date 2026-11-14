package io.netty.handler.codec.spdy;

import com.jcraft.jzlib.Deflater;
import com.jcraft.jzlib.JZlib;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.compression.CompressionException;

class SpdyHeaderBlockJZlibEncoder extends SpdyHeaderBlockRawEncoder {

    private final Deflater z = new Deflater();
    private boolean finished;

    public SpdyHeaderBlockJZlibEncoder(int i, int j, int k, int l) {
        super(i);
        if (j >= 0 && j <= 9) {
            if (k >= 9 && k <= 15) {
                if (l >= 1 && l <= 9) {
                    int i1 = this.z.deflateInit(j, k, l, JZlib.W_ZLIB);

                    if (i1 != 0) {
                        throw new CompressionException("failed to initialize an SPDY header block deflater: " + i1);
                    } else {
                        if (i < 3) {
                            i1 = this.z.deflateSetDictionary(SpdyCodecUtil.SPDY2_DICT, SpdyCodecUtil.SPDY2_DICT.length);
                        } else {
                            i1 = this.z.deflateSetDictionary(SpdyCodecUtil.SPDY_DICT, SpdyCodecUtil.SPDY_DICT.length);
                        }

                        if (i1 != 0) {
                            throw new CompressionException("failed to set the SPDY dictionary: " + i1);
                        }
                    }
                } else {
                    throw new IllegalArgumentException("memLevel: " + l + " (expected: 1-9)");
                }
            } else {
                throw new IllegalArgumentException("windowBits: " + k + " (expected: 9-15)");
            }
        } else {
            throw new IllegalArgumentException("compressionLevel: " + j + " (expected: 0-9)");
        }
    }

    private void setInput(ByteBuf bytebuf) {
        byte[] abyte = new byte[bytebuf.readableBytes()];

        bytebuf.readBytes(abyte);
        this.z.next_in = abyte;
        this.z.next_in_index = 0;
        this.z.avail_in = abyte.length;
    }

    private void encode(ByteBuf bytebuf) {
        try {
            byte[] abyte = new byte[(int) Math.ceil((double) this.z.next_in.length * 1.001D) + 12];

            this.z.next_out = abyte;
            this.z.next_out_index = 0;
            this.z.avail_out = abyte.length;
            int i = this.z.deflate(2);

            if (i != 0) {
                throw new CompressionException("compression failure: " + i);
            }

            if (this.z.next_out_index != 0) {
                bytebuf.writeBytes(abyte, 0, this.z.next_out_index);
            }
        } finally {
            this.z.next_in = null;
            this.z.next_out = null;
        }

    }

    @Override
    public ByteBuf encode(ChannelHandlerContext channelhandlercontext, SpdyHeadersFrame spdyheadersframe) throws Exception {
        if (spdyheadersframe == null) {
            throw new IllegalArgumentException("frame");
        } else if (this.finished) {
            return Unpooled.EMPTY_BUFFER;
        } else {
            ByteBuf bytebuf = super.encode(channelhandlercontext, spdyheadersframe);

            if (bytebuf.readableBytes() == 0) {
                return Unpooled.EMPTY_BUFFER;
            } else {
                ByteBuf bytebuf1 = channelhandlercontext.alloc().buffer();

                this.setInput(bytebuf);
                this.encode(bytebuf1);
                return bytebuf1;
            }
        }
    }

    @Override
    public void end() {
        if (!this.finished) {
            this.finished = true;
            this.z.deflateEnd();
            this.z.next_in = null;
            this.z.next_out = null;
        }
    }
}
