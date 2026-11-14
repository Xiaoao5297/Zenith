package io.netty.handler.codec.socks;

public enum SocksCmdType {

    CONNECT((byte) 1), BIND((byte) 2), UDP((byte) 3), UNKNOWN((byte) -1);

    private final byte b;

    private SocksCmdType(byte b0) {
        this.b = b0;
    }

    public static SocksCmdType fromByte(byte b0) {
        SocksCmdType[] asockscmdtype = values();
        int i = asockscmdtype.length;

        for (int j = 0; j < i; ++j) {
            SocksCmdType sockscmdtype = asockscmdtype[j];

            if (sockscmdtype.b == b0) {
                return sockscmdtype;
            }
        }

        return SocksCmdType.UNKNOWN;
    }

    public byte byteValue() {
        return this.b;
    }
}
