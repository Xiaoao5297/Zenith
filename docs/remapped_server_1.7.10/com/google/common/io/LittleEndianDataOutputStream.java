package com.google.common.io;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;
import com.google.common.primitives.Longs;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

@Beta
public class LittleEndianDataOutputStream extends FilterOutputStream implements DataOutput {

    public LittleEndianDataOutputStream(OutputStream outputstream) {
        super(new DataOutputStream((OutputStream) Preconditions.checkNotNull(outputstream)));
    }

    public void write(byte[] abyte, int i, int j) throws IOException {
        this.out.write(abyte, i, j);
    }

    public void writeBoolean(boolean flag) throws IOException {
        ((DataOutputStream) this.out).writeBoolean(flag);
    }

    public void writeByte(int i) throws IOException {
        ((DataOutputStream) this.out).writeByte(i);
    }

    /** @deprecated */
    @Deprecated
    public void writeBytes(String s) throws IOException {
        ((DataOutputStream) this.out).writeBytes(s);
    }

    public void writeChar(int i) throws IOException {
        this.writeShort(i);
    }

    public void writeChars(String s) throws IOException {
        for (int i = 0; i < s.length(); ++i) {
            this.writeChar(s.charAt(i));
        }

    }

    public void writeDouble(double d0) throws IOException {
        this.writeLong(Double.doubleToLongBits(d0));
    }

    public void writeFloat(float f) throws IOException {
        this.writeInt(Float.floatToIntBits(f));
    }

    public void writeInt(int i) throws IOException {
        this.out.write(255 & i);
        this.out.write(255 & i >> 8);
        this.out.write(255 & i >> 16);
        this.out.write(255 & i >> 24);
    }

    public void writeLong(long i) throws IOException {
        byte[] abyte = Longs.toByteArray(Long.reverseBytes(i));

        this.write(abyte, 0, abyte.length);
    }

    public void writeShort(int i) throws IOException {
        this.out.write(255 & i);
        this.out.write(255 & i >> 8);
    }

    public void writeUTF(String s) throws IOException {
        ((DataOutputStream) this.out).writeUTF(s);
    }
}
