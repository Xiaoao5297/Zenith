package io.netty.handler.stream;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import java.io.InputStream;
import java.io.PushbackInputStream;

public class ChunkedStream implements ChunkedInput<ByteBuf> {

    static final int DEFAULT_CHUNK_SIZE = 8192;
    private final PushbackInputStream in;
    private final int chunkSize;
    private long offset;

    public ChunkedStream(InputStream inputstream) {
        this(inputstream, 8192);
    }

    public ChunkedStream(InputStream inputstream, int i) {
        if (inputstream == null) {
            throw new NullPointerException("in");
        } else if (i <= 0) {
            throw new IllegalArgumentException("chunkSize: " + i + " (expected: a positive integer)");
        } else {
            if (inputstream instanceof PushbackInputStream) {
                this.in = (PushbackInputStream) inputstream;
            } else {
                this.in = new PushbackInputStream(inputstream);
            }

            this.chunkSize = i;
        }
    }

    public long transferredBytes() {
        return this.offset;
    }

    @Override
    public boolean isEndOfInput() throws Exception {
        int i = this.in.read();

        if (i < 0) {
            return true;
        } else {
            this.in.unread(i);
            return false;
        }
    }

    @Override
    public void close() throws Exception {
        this.in.close();
    }

    @Override
    public ByteBuf readChunk(ChannelHandlerContext channelhandlercontext) throws Exception {
        if (this.isEndOfInput()) {
            return null;
        } else {
            int i = this.in.available();
            int j;

            if (i <= 0) {
                j = this.chunkSize;
            } else {
                j = Math.min(this.chunkSize, this.in.available());
            }

            boolean flag = true;
            ByteBuf bytebuf = channelhandlercontext.alloc().buffer(j);

            ByteBuf bytebuf1;

            try {
                this.offset += (long) bytebuf.writeBytes((InputStream) this.in, j);
                flag = false;
                bytebuf1 = bytebuf;
            } finally {
                if (flag) {
                    bytebuf.release();
                }

            }

            return bytebuf1;
        }
    }
}
