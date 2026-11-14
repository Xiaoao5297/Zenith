package io.netty.buffer;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

public class ByteBufInputStream extends InputStream implements DataInput {

    private final ByteBuf buffer;
    private final int startIndex;
    private final int endIndex;
    private final StringBuilder lineBuf;

    public ByteBufInputStream(ByteBuf bytebuf) {
        this(bytebuf, bytebuf.readableBytes());
    }

    public ByteBufInputStream(ByteBuf bytebuf, int i) {
        this.lineBuf = new StringBuilder();
        if (bytebuf == null) {
            throw new NullPointerException("buffer");
        } else if (i < 0) {
            throw new IllegalArgumentException("length: " + i);
        } else if (i > bytebuf.readableBytes()) {
            throw new IndexOutOfBoundsException("Too many bytes to be read - Needs " + i + ", maximum is " + bytebuf.readableBytes());
        } else {
            this.buffer = bytebuf;
            this.startIndex = bytebuf.readerIndex();
            this.endIndex = this.startIndex + i;
            bytebuf.markReaderIndex();
        }
    }

    public int readBytes() {
        return this.buffer.readerIndex() - this.startIndex;
    }

    public int available() throws IOException {
        return this.endIndex - this.buffer.readerIndex();
    }

    public void mark(int i) {
        this.buffer.markReaderIndex();
    }

    public boolean markSupported() {
        return true;
    }

    public int read() throws IOException {
        return !this.buffer.isReadable() ? -1 : this.buffer.readByte() & 255;
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

    public void reset() throws IOException {
        this.buffer.resetReaderIndex();
    }

    public long skip(long i) throws IOException {
        return i > 2147483647L ? (long) this.skipBytes(Integer.MAX_VALUE) : (long) this.skipBytes((int) i);
    }

    public boolean readBoolean() throws IOException {
        this.checkAvailable(1);
        return this.read() != 0;
    }

    public byte readByte() throws IOException {
        if (!this.buffer.isReadable()) {
            throw new EOFException();
        } else {
            return this.buffer.readByte();
        }
    }

    public char readChar() throws IOException {
        return (char) this.readShort();
    }

    public double readDouble() throws IOException {
        return Double.longBitsToDouble(this.readLong());
    }

    public float readFloat() throws IOException {
        return Float.intBitsToFloat(this.readInt());
    }

    public void readFully(byte[] abyte) throws IOException {
        this.readFully(abyte, 0, abyte.length);
    }

    public void readFully(byte[] abyte, int i, int j) throws IOException {
        this.checkAvailable(j);
        this.buffer.readBytes(abyte, i, j);
    }

    public int readInt() throws IOException {
        this.checkAvailable(4);
        return this.buffer.readInt();
    }

    public String readLine() throws IOException {
        this.lineBuf.setLength(0);

        while (this.buffer.isReadable()) {
            short short0 = this.buffer.readUnsignedByte();

            switch (short0) {
                case 13:
                    if (this.buffer.isReadable() && this.buffer.getUnsignedByte(this.buffer.readerIndex()) == 10) {
                        this.buffer.skipBytes(1);
                    }
                case 10:
                    return this.lineBuf.toString();
                default:
                    this.lineBuf.append((char) short0);
            }
        }

        return this.lineBuf.length() > 0 ? this.lineBuf.toString() : null;
    }

    public long readLong() throws IOException {
        this.checkAvailable(8);
        return this.buffer.readLong();
    }

    public short readShort() throws IOException {
        this.checkAvailable(2);
        return this.buffer.readShort();
    }

    public String readUTF() throws IOException {
        return DataInputStream.readUTF(this);
    }

    public int readUnsignedByte() throws IOException {
        return this.readByte() & 255;
    }

    public int readUnsignedShort() throws IOException {
        return this.readShort() & '\uffff';
    }

    public int skipBytes(int i) throws IOException {
        int j = Math.min(this.available(), i);

        this.buffer.skipBytes(j);
        return j;
    }

    private void checkAvailable(int i) throws IOException {
        if (i < 0) {
            throw new IndexOutOfBoundsException("fieldSize cannot be a negative number");
        } else if (i > this.available()) {
            throw new EOFException("fieldSize is too long! Length is " + i + ", but maximum is " + this.available());
        }
    }
}
