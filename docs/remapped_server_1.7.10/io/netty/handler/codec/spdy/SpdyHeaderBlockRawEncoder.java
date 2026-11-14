package io.netty.handler.codec.spdy;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import java.util.Iterator;
import java.util.Set;

public class SpdyHeaderBlockRawEncoder extends SpdyHeaderBlockEncoder {

    private final int version;

    public SpdyHeaderBlockRawEncoder(int i) {
        if (i >= 2 && i <= 3) {
            this.version = i;
        } else {
            throw new IllegalArgumentException("unknown version: " + i);
        }
    }

    private void setLengthField(ByteBuf bytebuf, int i, int j) {
        if (this.version < 3) {
            bytebuf.setShort(i, j);
        } else {
            bytebuf.setInt(i, j);
        }

    }

    private void writeLengthField(ByteBuf bytebuf, int i) {
        if (this.version < 3) {
            bytebuf.writeShort(i);
        } else {
            bytebuf.writeInt(i);
        }

    }

    @Override
    public ByteBuf encode(ChannelHandlerContext channelhandlercontext, SpdyHeadersFrame spdyheadersframe) throws Exception {
        Set set = spdyheadersframe.headers().names();
        int i = set.size();

        if (i == 0) {
            return Unpooled.EMPTY_BUFFER;
        } else if (i > 65535) {
            throw new IllegalArgumentException("header block contains too many headers");
        } else {
            ByteBuf bytebuf = Unpooled.buffer();

            this.writeLengthField(bytebuf, i);
            Iterator iterator = set.iterator();

            while (iterator.hasNext()) {
                String s = (String) iterator.next();
                byte[] abyte = s.getBytes("UTF-8");

                this.writeLengthField(bytebuf, abyte.length);
                bytebuf.writeBytes(abyte);
                int j = bytebuf.writerIndex();
                int k = 0;

                this.writeLengthField(bytebuf, k);
                Iterator iterator1 = spdyheadersframe.headers().getAll(s).iterator();

                while (iterator1.hasNext()) {
                    String s1 = (String) iterator1.next();
                    byte[] abyte1 = s1.getBytes("UTF-8");

                    if (abyte1.length > 0) {
                        bytebuf.writeBytes(abyte1);
                        bytebuf.writeByte(0);
                        k += abyte1.length + 1;
                    }
                }

                if (k == 0) {
                    if (this.version < 3) {
                        throw new IllegalArgumentException("header value cannot be empty: " + s);
                    }
                } else {
                    --k;
                }

                if (k > 65535) {
                    throw new IllegalArgumentException("header exceeds allowable length: " + s);
                }

                if (k > 0) {
                    this.setLengthField(bytebuf, j, k);
                    bytebuf.writerIndex(bytebuf.writerIndex() - 1);
                }
            }

            return bytebuf;
        }
    }

    @Override
    void end() {}
}
