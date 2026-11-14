package io.netty.handler.codec.socks;

import io.netty.buffer.ByteBuf;

public abstract class SocksMessage {

    private final SocksMessageType type;
    private final SocksProtocolVersion protocolVersion;

    protected SocksMessage(SocksMessageType socksmessagetype) {
        this.protocolVersion = SocksProtocolVersion.SOCKS5;
        if (socksmessagetype == null) {
            throw new NullPointerException("type");
        } else {
            this.type = socksmessagetype;
        }
    }

    public SocksMessageType type() {
        return this.type;
    }

    public SocksProtocolVersion protocolVersion() {
        return this.protocolVersion;
    }

    public abstract void encodeAsByteBuf(ByteBuf bytebuf);
}
