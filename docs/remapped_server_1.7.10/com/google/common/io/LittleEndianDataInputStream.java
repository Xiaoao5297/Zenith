package com.google.common.io;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;
import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

@Beta
public final class LittleEndianDataInputStream extends FilterInputStream implements DataInput {

    public LittleEndianDataInputStream(InputStream inputstream) {
        super((InputStream) Preconditions.checkNotNull(inputstream));
    }

    public String readLine() {
        throw new UnsupportedOperationException("readLine is not supported");
    }

    public void readFully(byte[] abyte) throws IOException {
        ByteStreams.readFully(this, abyte);
    }

    public void readFully(byte[] abyte, int i, int j) throws IOException {
        ByteStreams.readFully(this, abyte, i, j);
    }

    public int skipBytes(int i) throws IOException {
        return (int) this.in.skip((long) i);
    }

    public int readUnsignedByte() throws IOException {
        int i = this.in.read();

        if (0 > i) {
            throw new EOFException();
        } else {
            return i;
        }
    }

    public int readUnsignedShort() throws IOException {
        byte b0 = this.readAndCheckByte();
        byte b1 = this.readAndCheckByte();

        return Ints.fromBytes((byte) 0, (byte) 0, b1, b0);
    }

    public int readInt() throws IOException {
        byte b0 = this.readAndCheckByte();
        byte b1 = this.readAndCheckByte();
        byte b2 = this.readAndCheckByte();
        byte b3 = this.readAndCheckByte();

        return Ints.fromBytes(b3, b2, b1, b0);
    }

    public long readLong() throws IOException {
        byte b0 = this.readAndCheckByte();
        byte b1 = this.readAndCheckByte();
        byte b2 = this.readAndCheckByte();
        byte b3 = this.readAndCheckByte();
        byte b4 = this.readAndCheckByte();
        byte b5 = this.readAndCheckByte();
        byte b6 = this.readAndCheckByte();
        byte b7 = this.readAndCheckByte();

        return Longs.fromBytes(b7, b6, b5, b4, b3, b2, b1, b0);
    }

    public float readFloat() throws IOException {
        return Float.intBitsToFloat(this.readInt());
    }

    public double readDouble() throws IOException {
        return Double.longBitsToDouble(this.readLong());
    }

    public String readUTF() throws IOException {
        return (new DataInputStream(this.in)).readUTF();
    }

    public short readShort() throws IOException {
        return (short) this.readUnsignedShort();
    }

    public char readChar() throws IOException {
        return (char) this.readUnsignedShort();
    }

    public byte readByte() throws IOException {
        return (byte) this.readUnsignedByte();
    }

    public boolean readBoolean() throws IOException {
        return this.readUnsignedByte() != 0;
    }

    private byte readAndCheckByte() throws IOException, EOFException {
        int i = this.in.read();

        if (-1 == i) {
            throw new EOFException();
        } else {
            return (byte) i;
        }
    }
}
