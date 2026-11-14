package io.netty.handler.stream;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;

public class ChunkedNioStream implements ChunkedInput<ByteBuf> {

    private final ReadableByteChannel in;
    private final int chunkSize;
    private long offset;
    private final ByteBuffer byteBuffer;

    public ChunkedNioStream(ReadableByteChannel readablebytechannel) {
        this(readablebytechannel, 8192);
    }

    public ChunkedNioStream(ReadableByteChannel readablebytechannel, int i) {
        if (readablebytechannel == null) {
            throw new NullPointerException("in");
        } else if (i <= 0) {
            throw new IllegalArgumentException("chunkSize: " + i + " (expected: a positive integer)");
        } else {
            this.in = readablebytechannel;
            this.offset = 0L;
            this.chunkSize = i;
            this.byteBuffer = ByteBuffer.allocate(i);
        }
    }

    public long transferredBytes() {
        return this.offset;
    }

    @Override
    public boolean isEndOfInput() throws Exception {
        if (this.byteBuffer.position() > 0) {
            return false;
        } else if (this.in.isOpen()) {
            int i = this.in.read(this.byteBuffer);

            if (i < 0) {
                return true;
            } else {
                this.offset += (long) i;
                return false;
            }
        } else {
            return true;
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
            int i = this.byteBuffer.position();

            do {
                int j = this.in.read(this.byteBuffer);

                if (j < 0) {
                    break;
                }

                i += j;
                this.offset += (long) j;
            } while (i != this.chunkSize);

            this.byteBuffer.flip();
            boolean flag = true;
            ByteBuf bytebuf = channelhandlercontext.alloc().buffer(this.byteBuffer.remaining());

            ByteBuf bytebuf1;

            try {
                bytebuf.writeBytes(this.byteBuffer);
                this.byteBuffer.clear();
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
