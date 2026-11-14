package io.netty.handler.codec.socks;

import io.netty.buffer.ByteBuf;

public final class SocksCmdResponse extends SocksResponse {

    private final SocksCmdStatus cmdStatus;
    private final SocksAddressType addressType;
    private static final byte[] IPv4_HOSTNAME_ZEROED = new byte[]{0, 0, 0, 0};
    private static final byte[] IPv6_HOSTNAME_ZEROED = new byte[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

    public SocksCmdResponse(SocksCmdStatus sockscmdstatus, SocksAddressType socksaddresstype) {
        super(SocksResponseType.CMD);
        if (sockscmdstatus == null) {
            throw new NullPointerException("cmdStatus");
        } else if (socksaddresstype == null) {
            throw new NullPointerException("addressType");
        } else {
            this.cmdStatus = sockscmdstatus;
            this.addressType = socksaddresstype;
        }
    }

    public SocksCmdStatus cmdStatus() {
        return this.cmdStatus;
    }

    public SocksAddressType addressType() {
        return this.addressType;
    }

    @Override
    public void encodeAsByteBuf(ByteBuf bytebuf) {
        bytebuf.writeByte(this.protocolVersion().byteValue());
        bytebuf.writeByte(this.cmdStatus.byteValue());
        bytebuf.writeByte(0);
        bytebuf.writeByte(this.addressType.byteValue());
        switch (this.addressType) {
            case IPv4:
                bytebuf.writeBytes(SocksCmdResponse.IPv4_HOSTNAME_ZEROED);
                bytebuf.writeShort(0);
                break;
            case DOMAIN:
                bytebuf.writeByte(1);
                bytebuf.writeByte(0);
                bytebuf.writeShort(0);
                break;
            case IPv6:
                bytebuf.writeBytes(SocksCmdResponse.IPv6_HOSTNAME_ZEROED);
                bytebuf.writeShort(0);
        }

    }
}
