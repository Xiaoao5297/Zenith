package io.netty.handler.codec.serialization;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.OutputStream;

public class ObjectEncoderOutputStream extends OutputStream implements ObjectOutput {

    private final DataOutputStream out;
    private final int estimatedLength;

    public ObjectEncoderOutputStream(OutputStream outputstream) {
        this(outputstream, 512);
    }

    public ObjectEncoderOutputStream(OutputStream outputstream, int i) {
        if (outputstream == null) {
            throw new NullPointerException("out");
        } else if (i < 0) {
            throw new IllegalArgumentException("estimatedLength: " + i);
        } else {
            if (outputstream instanceof DataOutputStream) {
                this.out = (DataOutputStream) outputstream;
            } else {
                this.out = new DataOutputStream(outputstream);
            }

            this.estimatedLength = i;
        }
    }

    public void writeObject(Object object) throws IOException {
        ByteBufOutputStream bytebufoutputstream = new ByteBufOutputStream(Unpooled.buffer(this.estimatedLength));
        CompactObjectOutputStream compactobjectoutputstream = new CompactObjectOutputStream(bytebufoutputstream);

        compactobjectoutputstream.writeObject(object);
        compactobjectoutputstream.flush();
        compactobjectoutputstream.close();
        ByteBuf bytebuf = bytebufoutputstream.buffer();
        int i = bytebuf.readableBytes();

        this.writeInt(i);
        bytebuf.getBytes(0, (OutputStream) this, i);
    }

    public void write(int i) throws IOException {
        this.out.write(i);
    }

    public void close() throws IOException {
        this.out.close();
    }

    public void flush() throws IOException {
        this.out.flush();
    }

    public final int size() {
        return this.out.size();
    }

    public void write(byte[] abyte, int i, int j) throws IOException {
        this.out.write(abyte, i, j);
    }

    public void write(byte[] abyte) throws IOException {
        this.out.write(abyte);
    }

    public final void writeBoolean(boolean flag) throws IOException {
        this.out.writeBoolean(flag);
    }

    public final void writeByte(int i) throws IOException {
        this.out.writeByte(i);
    }

    public final void writeBytes(String s) throws IOException {
        this.out.writeBytes(s);
    }

    public final void writeChar(int i) throws IOException {
        this.out.writeChar(i);
    }

    public final void writeChars(String s) throws IOException {
        this.out.writeChars(s);
    }

    public final void writeDouble(double d0) throws IOException {
        this.out.writeDouble(d0);
    }

    public final void writeFloat(float f) throws IOException {
        this.out.writeFloat(f);
    }

    public final void writeInt(int i) throws IOException {
        this.out.writeInt(i);
    }

    public final void writeLong(long i) throws IOException {
        this.out.writeLong(i);
    }

    public final void writeShort(int i) throws IOException {
        this.out.writeShort(i);
    }

    public final void writeUTF(String s) throws IOException {
        this.out.writeUTF(s);
    }
}
