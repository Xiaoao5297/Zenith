package io.netty.handler.codec.socks;

import io.netty.buffer.ByteBuf;

public final class SocksAuthResponse extends SocksResponse {

    private static final SocksSubnegotiationVersion SUBNEGOTIATION_VERSION = SocksSubnegotiationVersion.AUTH_PASSWORD;
    private final SocksAuthStatus authStatus;

    public SocksAuthResponse(SocksAuthStatus socksauthstatus) {
        super(SocksResponseType.AUTH);
        if (socksauthstatus == null) {
            throw new NullPointerException("authStatus");
        } else {
            this.authStatus = socksauthstatus;
        }
    }

    public SocksAuthStatus authStatus() {
        return this.authStatus;
    }

    @Override
    public void encodeAsByteBuf(ByteBuf bytebuf) {
        bytebuf.writeByte(SocksAuthResponse.SUBNEGOTIATION_VERSION.byteValue());
        bytebuf.writeByte(this.authStatus.byteValue());
    }
}
