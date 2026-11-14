package io.netty.buffer;

import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class ByteBufOutputStream extends OutputStream implements DataOutput {

    private final ByteBuf buffer;
    private final int startIndex;
    private final DataOutputStream utf8out = new DataOutputStream(this);

    public ByteBufOutputStream(ByteBuf bytebuf) {
        if (bytebuf == null) {
            throw new NullPointerException("buffer");
        } else {
            this.buffer = bytebuf;
            this.startIndex = bytebuf.writerIndex();
        }
    }

    public int writtenBytes() {
        return this.buffer.writerIndex() - this.startIndex;
    }

    public void write(byte[] abyte, int i, int j) throws IOException {
        if (j != 0) {
            this.buffer.writeBytes(abyte, i, j);
        }
    }

    public void write(byte[] abyte) throws IOException {
        this.buffer.writeBytes(abyte);
    }

    public void write(int i) throws IOException {
        this.buffer.writeByte((byte) i);
    }

    public void writeBoolean(boolean flag) throws IOException {
        this.write(flag ? 1 : 0);
    }

    public void writeByte(int i) throws IOException {
        this.write(i);
    }

    public void writeBytes(String s) throws IOException {
        int i = s.length();

        for (int j = 0; j < i; ++j) {
            this.write((byte) s.charAt(j));
        }

    }

    public void writeChar(int i) throws IOException {
        this.writeShort((short) i);
    }

    public void writeChars(String s) throws IOException {
        int i = s.length();

        for (int j = 0; j < i; ++j) {
            this.writeChar(s.charAt(j));
        }

    }

    public void writeDouble(double d0) throws IOException {
        this.writeLong(Double.doubleToLongBits(d0));
    }

    public void writeFloat(float f) throws IOException {
        this.writeInt(Float.floatToIntBits(f));
    }

    public void writeInt(int i) throws IOException {
        this.buffer.writeInt(i);
    }

    public void writeLong(long i) throws IOException {
        this.buffer.writeLong(i);
    }

    public void writeShort(int i) throws IOException {
        this.buffer.writeShort((short) i);
    }

    public void writeUTF(String s) throws IOException {
        this.utf8out.writeUTF(s);
    }

    public ByteBuf buffer() {
        return this.buffer;
    }
}
