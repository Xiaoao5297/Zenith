package com.google.common.io;

import java.io.DataOutput;

public interface ByteArrayDataOutput extends DataOutput {

    void write(int i);

    void write(byte[] abyte);

    void write(byte[] abyte, int i, int j);

    void writeBoolean(boolean flag);

    void writeByte(int i);

    void writeShort(int i);

    void writeChar(int i);

    void writeInt(int i);

    void writeLong(long i);

    void writeFloat(float f);

    void writeDouble(double d0);

    void writeChars(String s);

    void writeUTF(String s);

    /** @deprecated */
    @Deprecated
    void writeBytes(String s);

    byte[] toByteArray();
}
