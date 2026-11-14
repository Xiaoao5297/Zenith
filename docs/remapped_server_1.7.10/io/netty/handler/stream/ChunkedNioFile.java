package io.netty.handler.stream;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.ScatteringByteChannel;

public class ChunkedNioFile implements ChunkedInput<ByteBuf> {

    private final FileChannel in;
    private final long startOffset;
    private final long endOffset;
    private final int chunkSize;
    private long offset;

    public ChunkedNioFile(File file) throws IOException {
        this((new FileInputStream(file)).getChannel());
    }

    public ChunkedNioFile(File file, int i) throws IOException {
        this((new FileInputStream(file)).getChannel(), i);
    }

    public ChunkedNioFile(FileChannel filechannel) throws IOException {
        this(filechannel, 8192);
    }

    public ChunkedNioFile(FileChannel filechannel, int i) throws IOException {
        this(filechannel, 0L, filechannel.size(), i);
    }

    public ChunkedNioFile(FileChannel filechannel, long i, long j, int k) throws IOException {
        if (filechannel == null) {
            throw new NullPointerException("in");
        } else if (i < 0L) {
            throw new IllegalArgumentException("offset: " + i + " (expected: 0 or greater)");
        } else if (j < 0L) {
            throw new IllegalArgumentException("length: " + j + " (expected: 0 or greater)");
        } else if (k <= 0) {
            throw new IllegalArgumentException("chunkSize: " + k + " (expected: a positive integer)");
        } else {
            if (i != 0L) {
                filechannel.position(i);
            }

            this.in = filechannel;
            this.chunkSize = k;
            this.offset = this.startOffset = i;
            this.endOffset = i + j;
        }
    }

    public long startOffset() {
        return this.startOffset;
    }

    public long endOffset() {
        return this.endOffset;
    }

    public long currentOffset() {
        return this.offset;
    }

    @Override
    public boolean isEndOfInput() throws Exception {
        return this.offset >= this.endOffset || !this.in.isOpen();
    }

    @Override
    public void close() throws Exception {
        this.in.close();
    }

    @Override
    public ByteBuf readChunk(ChannelHandlerContext channelhandlercontext) throws Exception {
        long i = this.offset;

        if (i >= this.endOffset) {
            return null;
        } else {
            int j = (int) Math.min((long) this.chunkSize, this.endOffset - i);
            ByteBuf bytebuf = channelhandlercontext.alloc().buffer(j);
            boolean flag = true;

            ByteBuf bytebuf1;

            try {
                int k = 0;

                do {
                    int l = bytebuf.writeBytes((ScatteringByteChannel) this.in, j - k);

                    if (l < 0) {
                        break;
                    }

                    k += l;
                } while (k != j);

                this.offset += (long) k;
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
