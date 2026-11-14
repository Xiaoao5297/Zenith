package io.netty.handler.codec.socks;

public enum SocksAddressType {

    IPv4((byte) 1), DOMAIN((byte) 3), IPv6((byte) 4), UNKNOWN((byte) -1);

    private final byte b;

    private SocksAddressType(byte b0) {
        this.b = b0;
    }

    public static SocksAddressType fromByte(byte b0) {
        SocksAddressType[] asocksaddresstype = values();
        int i = asocksaddresstype.length;

        for (int j = 0; j < i; ++j) {
            SocksAddressType socksaddresstype = asocksaddresstype[j];

            if (socksaddresstype.b == b0) {
                return socksaddresstype;
            }
        }

        return SocksAddressType.UNKNOWN;
    }

    public byte byteValue() {
        return this.b;
    }
}
