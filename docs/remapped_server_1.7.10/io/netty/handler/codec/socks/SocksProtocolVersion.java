package io.netty.handler.codec.socks;

public enum SocksProtocolVersion {

    SOCKS4a((byte) 4), SOCKS5((byte) 5), UNKNOWN((byte) -1);

    private final byte b;

    private SocksProtocolVersion(byte b0) {
        this.b = b0;
    }

    public static SocksProtocolVersion fromByte(byte b0) {
        SocksProtocolVersion[] asocksprotocolversion = values();
        int i = asocksprotocolversion.length;

        for (int j = 0; j < i; ++j) {
            SocksProtocolVersion socksprotocolversion = asocksprotocolversion[j];

            if (socksprotocolversion.b == b0) {
                return socksprotocolversion;
            }
        }

        return SocksProtocolVersion.UNKNOWN;
    }

    public byte byteValue() {
        return this.b;
    }
}
