package io.netty.handler.codec.serialization;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.StreamCorruptedException;

public class ObjectDecoderInputStream extends InputStream implements ObjectInput {

    private final DataInputStream in;
    private final int maxObjectSize;
    private final ClassResolver classResolver;

    public ObjectDecoderInputStream(InputStream inputstream) {
        this(inputstream, (ClassLoader) null);
    }

    public ObjectDecoderInputStream(InputStream inputstream, ClassLoader classloader) {
        this(inputstream, classloader, 1048576);
    }

    public ObjectDecoderInputStream(InputStream inputstream, int i) {
        this(inputstream, (ClassLoader) null, i);
    }

    public ObjectDecoderInputStream(InputStream inputstream, ClassLoader classloader, int i) {
        if (inputstream == null) {
            throw new NullPointerException("in");
        } else if (i <= 0) {
            throw new IllegalArgumentException("maxObjectSize: " + i);
        } else {
            if (inputstream instanceof DataInputStream) {
                this.in = (DataInputStream) inputstream;
            } else {
                this.in = new DataInputStream(inputstream);
            }

            this.classResolver = ClassResolvers.weakCachingResolver(classloader);
            this.maxObjectSize = i;
        }
    }

    public Object readObject() throws ClassNotFoundException, IOException {
        int i = this.readInt();

        if (i <= 0) {
            throw new StreamCorruptedException("invalid data length: " + i);
        } else if (i > this.maxObjectSize) {
            throw new StreamCorruptedException("data length too big: " + i + " (max: " + this.maxObjectSize + ')');
        } else {
            return (new CompactObjectInputStream(this.in, this.classResolver)).readObject();
        }
    }

    public int available() throws IOException {
        return this.in.available();
    }

    public void close() throws IOException {
        this.in.close();
    }

    public void mark(int i) {
        this.in.mark(i);
    }

    public boolean markSupported() {
        return this.in.markSupported();
    }

    public int read() throws IOException {
        return this.in.read();
    }

    public final int read(byte[] abyte, int i, int j) throws IOException {
        return this.in.read(abyte, i, j);
    }

    public final int read(byte[] abyte) throws IOException {
        return this.in.read(abyte);
    }

    public final boolean readBoolean() throws IOException {
        return this.in.readBoolean();
    }

    public final byte readByte() throws IOException {
        return this.in.readByte();
    }

    public final char readChar() throws IOException {
        return this.in.readChar();
    }

    public final double readDouble() throws IOException {
        return this.in.readDouble();
    }

    public final float readFloat() throws IOException {
        return this.in.readFloat();
    }

    public final void readFully(byte[] abyte, int i, int j) throws IOException {
        this.in.readFully(abyte, i, j);
    }

    public final void readFully(byte[] abyte) throws IOException {
        this.in.readFully(abyte);
    }

    public final int readInt() throws IOException {
        return this.in.readInt();
    }

    /** @deprecated */
    @Deprecated
    public final String readLine() throws IOException {
        return this.in.readLine();
    }

    public final long readLong() throws IOException {
        return this.in.readLong();
    }

    public final short readShort() throws IOException {
        return this.in.readShort();
    }

    public final int readUnsignedByte() throws IOException {
        return this.in.readUnsignedByte();
    }

    public final int readUnsignedShort() throws IOException {
        return this.in.readUnsignedShort();
    }

    public final String readUTF() throws IOException {
        return this.in.readUTF();
    }

    public void reset() throws IOException {
        this.in.reset();
    }

    public long skip(long i) throws IOException {
        return this.in.skip(i);
    }

    public final int skipBytes(int i) throws IOException {
        return this.in.skipBytes(i);
    }
}
