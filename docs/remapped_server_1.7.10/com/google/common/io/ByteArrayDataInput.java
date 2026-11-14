package com.google.common.io;

import java.io.DataInput;

public interface ByteArrayDataInput extends DataInput {

    void readFully(byte[] abyte);

    void readFully(byte[] abyte, int i, int j);

    int skipBytes(int i);

    boolean readBoolean();

    byte readByte();

    int readUnsignedByte();

    short readShort();

    int readUnsignedShort();

    char readChar();

    int readInt();

    long readLong();

    float readFloat();

    double readDouble();

    String readLine();

    String readUTF();
}
