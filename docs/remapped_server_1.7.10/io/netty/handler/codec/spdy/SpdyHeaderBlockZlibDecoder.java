package io.netty.handler.codec.spdy;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

class SpdyHeaderBlockZlibDecoder extends SpdyHeaderBlockRawDecoder {

    private final int version;
    private final byte[] out = new byte[8192];
    private final Inflater decompressor = new Inflater();
    private ByteBuf decompressed;

    public SpdyHeaderBlockZlibDecoder(int i, int j) {
        super(i, j);
        this.version = i;
    }

    @Override
    void decode(ByteBuf bytebuf, SpdyHeadersFrame spdyheadersframe) throws Exception {
        this.setInput(bytebuf);

        int i;

        do {
            i = this.decompress(spdyheadersframe);
        } while (!this.decompressed.isReadable() && i > 0);

    }

    private void setInput(ByteBuf bytebuf) {
        byte[] abyte = new byte[bytebuf.readableBytes()];

        bytebuf.readBytes(abyte);
        this.decompressor.setInput(abyte);
    }

    private int decompress(SpdyHeadersFrame spdyheadersframe) throws Exception {
        if (this.decompressed == null) {
            this.decompressed = Unpooled.buffer(8192);
        }

        try {
            int i = this.decompressor.inflate(this.out);

            if (i == 0 && this.decompressor.needsDictionary()) {
                if (this.version < 3) {
                    this.decompressor.setDictionary(SpdyCodecUtil.SPDY2_DICT);
                } else {
                    this.decompressor.setDictionary(SpdyCodecUtil.SPDY_DICT);
                }

                i = this.decompressor.inflate(this.out);
            }

            if (spdyheadersframe != null) {
                this.decompressed.writeBytes(this.out, 0, i);
                super.decode(this.decompressed, spdyheadersframe);
                this.decompressed.discardReadBytes();
            }

            return i;
        } catch (DataFormatException dataformatexception) {
            throw new SpdyProtocolException("Received invalid header block", dataformatexception);
        }
    }

    @Override
    public void reset() {
        this.decompressed = null;
        super.reset();
    }

    @Override
    public void end() {
        this.decompressed = null;
        this.decompressor.end();
        super.end();
    }
}
