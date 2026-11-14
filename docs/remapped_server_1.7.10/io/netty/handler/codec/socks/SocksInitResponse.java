package io.netty.handler.codec.socks;

import io.netty.buffer.ByteBuf;

public final class SocksInitResponse extends SocksResponse {

    private final SocksAuthScheme authScheme;

    public SocksInitResponse(SocksAuthScheme socksauthscheme) {
        super(SocksResponseType.INIT);
        if (socksauthscheme == null) {
            throw new NullPointerException("authScheme");
        } else {
            this.authScheme = socksauthscheme;
        }
    }

    public SocksAuthScheme authScheme() {
        return this.authScheme;
    }

    @Override
    public void encodeAsByteBuf(ByteBuf bytebuf) {
        bytebuf.writeByte(this.protocolVersion().byteValue());
        bytebuf.writeByte(this.authScheme.byteValue());
    }
}
