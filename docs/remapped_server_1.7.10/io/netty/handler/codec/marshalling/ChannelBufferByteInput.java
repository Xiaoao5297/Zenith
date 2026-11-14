package io.netty.handler.codec.marshalling;

import io.netty.buffer.ByteBuf;
import java.io.IOException;
import org.jboss.marshalling.ByteInput;

class ChannelBufferByteInput implements ByteInput {

    private final ByteBuf buffer;

    public ChannelBufferByteInput(ByteBuf bytebuf) {
        this.buffer = bytebuf;
    }

    public void close() throws IOException {}

    public int available() throws IOException {
        return this.buffer.readableBytes();
    }

    public int read() throws IOException {
        return this.buffer.isReadable() ? this.buffer.readByte() & 255 : -1;
    }

    public int read(byte[] abyte) throws IOException {
        return this.read(abyte, 0, abyte.length);
    }

    public int read(byte[] abyte, int i, int j) throws IOException {
        int k = this.available();

        if (k == 0) {
            return -1;
        } else {
            j = Math.min(k, j);
            this.buffer.readBytes(abyte, i, j);
            return j;
        }
    }

    public long skip(long i) throws IOException {
        int j = this.buffer.readableBytes();

        if ((long) j < i) {
            i = (long) j;
        }

        this.buffer.readerIndex((int) ((long) this.buffer.readerIndex() + i));
        return i;
    }
}
