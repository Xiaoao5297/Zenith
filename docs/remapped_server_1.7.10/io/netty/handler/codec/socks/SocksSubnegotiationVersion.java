package io.netty.handler.codec.socks;

public enum SocksSubnegotiationVersion {

    AUTH_PASSWORD((byte) 1), UNKNOWN((byte) -1);

    private final byte b;

    private SocksSubnegotiationVersion(byte b0) {
        this.b = b0;
    }

    public static SocksSubnegotiationVersion fromByte(byte b0) {
        SocksSubnegotiationVersion[] asockssubnegotiationversion = values();
        int i = asockssubnegotiationversion.length;

        for (int j = 0; j < i; ++j) {
            SocksSubnegotiationVersion sockssubnegotiationversion = asockssubnegotiationversion[j];

            if (sockssubnegotiationversion.b == b0) {
                return sockssubnegotiationversion;
            }
        }

        return SocksSubnegotiationVersion.UNKNOWN;
    }

    public byte byteValue() {
        return this.b;
    }
}
