package io.netty.handler.codec.socks;

public enum SocksAuthStatus {

    SUCCESS((byte) 0), FAILURE((byte) -1);

    private final byte b;

    private SocksAuthStatus(byte b0) {
        this.b = b0;
    }

    public static SocksAuthStatus fromByte(byte b0) {
        SocksAuthStatus[] asocksauthstatus = values();
        int i = asocksauthstatus.length;

        for (int j = 0; j < i; ++j) {
            SocksAuthStatus socksauthstatus = asocksauthstatus[j];

            if (socksauthstatus.b == b0) {
                return socksauthstatus;
            }
        }

        return SocksAuthStatus.FAILURE;
    }

    public byte byteValue() {
        return this.b;
    }
}
