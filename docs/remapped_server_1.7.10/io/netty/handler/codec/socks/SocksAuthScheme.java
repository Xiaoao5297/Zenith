package io.netty.handler.codec.socks;

public enum SocksAuthScheme {

    NO_AUTH((byte) 0), AUTH_GSSAPI((byte) 1), AUTH_PASSWORD((byte) 2), UNKNOWN((byte) -1);

    private final byte b;

    private SocksAuthScheme(byte b0) {
        this.b = b0;
    }

    public static SocksAuthScheme fromByte(byte b0) {
        SocksAuthScheme[] asocksauthscheme = values();
        int i = asocksauthscheme.length;

        for (int j = 0; j < i; ++j) {
            SocksAuthScheme socksauthscheme = asocksauthscheme[j];

            if (socksauthscheme.b == b0) {
                return socksauthscheme;
            }
        }

        return SocksAuthScheme.UNKNOWN;
    }

    public byte byteValue() {
        return this.b;
    }
}
