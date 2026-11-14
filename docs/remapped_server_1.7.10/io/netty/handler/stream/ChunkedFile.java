package io.netty.handler.stream;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class ChunkedFile implements ChunkedInput<ByteBuf> {

    private final RandomAccessFile file;
    private final long startOffset;
    private final long endOffset;
    private final int chunkSize;
    private long offset;

    public ChunkedFile(File file) throws IOException {
        this(file, 8192);
    }

    public ChunkedFile(File file, int i) throws IOException {
        this(new RandomAccessFile(file, "r"), i);
    }

    public ChunkedFile(RandomAccessFile randomaccessfile) throws IOException {
        this(randomaccessfile, 8192);
    }

    public ChunkedFile(RandomAccessFile randomaccessfile, int i) throws IOException {
        this(randomaccessfile, 0L, randomaccessfile.length(), i);
    }

    public ChunkedFile(RandomAccessFile randomaccessfile, long i, long j, int k) throws IOException {
        if (randomaccessfile == null) {
            throw new NullPointerException("file");
        } else if (i < 0L) {
            throw new IllegalArgumentException("offset: " + i + " (expected: 0 or greater)");
        } else if (j < 0L) {
            throw new IllegalArgumentException("length: " + j + " (expected: 0 or greater)");
        } else if (k <= 0) {
            throw new IllegalArgumentException("chunkSize: " + k + " (expected: a positive integer)");
        } else {
            this.file = randomaccessfile;
            this.offset = this.startOffset = i;
            this.endOffset = i + j;
            this.chunkSize = k;
            randomaccessfile.seek(i);
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
        return this.offset >= this.endOffset || !this.file.getChannel().isOpen();
    }

    @Override
    public void close() throws Exception {
        this.file.close();
    }

    @Override
    public ByteBuf readChunk(ChannelHandlerContext channelhandlercontext) throws Exception {
        long i = this.offset;

        if (i >= this.endOffset) {
            return null;
        } else {
            int j = (int) Math.min((long) this.chunkSize, this.endOffset - i);
            ByteBuf bytebuf = channelhandlercontext.alloc().heapBuffer(j);
            boolean flag = true;

            ByteBuf bytebuf1;

            try {
                this.file.readFully(bytebuf.array(), bytebuf.arrayOffset(), j);
                bytebuf.writerIndex(j);
                this.offset = i + (long) j;
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
