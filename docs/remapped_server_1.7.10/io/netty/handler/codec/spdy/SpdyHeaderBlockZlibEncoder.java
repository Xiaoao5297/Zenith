package io.netty.handler.codec.spdy;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import java.util.zip.Deflater;

class SpdyHeaderBlockZlibEncoder extends SpdyHeaderBlockRawEncoder {

    private final byte[] out = new byte[8192];
    private final Deflater compressor;
    private boolean finished;

    public SpdyHeaderBlockZlibEncoder(int i, int j) {
        super(i);
        if (j >= 0 && j <= 9) {
            this.compressor = new Deflater(j);
            if (i < 3) {
                this.compressor.setDictionary(SpdyCodecUtil.SPDY2_DICT);
            } else {
                this.compressor.setDictionary(SpdyCodecUtil.SPDY_DICT);
            }

        } else {
            throw new IllegalArgumentException("compressionLevel: " + j + " (expected: 0-9)");
        }
    }

    private void setInput(ByteBuf bytebuf) {
        byte[] abyte = new byte[bytebuf.readableBytes()];

        bytebuf.readBytes(abyte);
        this.compressor.setInput(abyte);
    }

    private void encode(ByteBuf bytebuf) {
        int i = this.out.length;

        while (i == this.out.length) {
            i = this.compressor.deflate(this.out, 0, this.out.length, 2);
            bytebuf.writeBytes(this.out, 0, i);
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
            this.compressor.end();
            super.end();
        }
    }
}
