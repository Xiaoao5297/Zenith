package io.netty.handler.codec.marshalling;

import io.netty.buffer.ByteBuf;
import java.io.IOException;
import org.jboss.marshalling.ByteOutput;

class ChannelBufferByteOutput implements ByteOutput {

    private final ByteBuf buffer;

    public ChannelBufferByteOutput(ByteBuf bytebuf) {
        this.buffer = bytebuf;
    }

    public void close() throws IOException {}

    public void flush() throws IOException {}

    public void write(int i) throws IOException {
        this.buffer.writeByte(i);
    }

    public void write(byte[] abyte) throws IOException {
        this.buffer.writeBytes(abyte);
    }

    public void write(byte[] abyte, int i, int j) throws IOException {
        this.buffer.writeBytes(abyte, i, j);
    }

    ByteBuf getBuffer() {
        return this.buffer;
    }
}
