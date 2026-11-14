package io.netty.handler.codec.socks;

import io.netty.buffer.ByteBuf;
import io.netty.util.CharsetUtil;
import io.netty.util.NetUtil;
import java.net.IDN;

public final class SocksCmdRequest extends SocksRequest {

    private final SocksCmdType cmdType;
    private final SocksAddressType addressType;
    private final String host;
    private final int port;

    public SocksCmdRequest(SocksCmdType sockscmdtype, SocksAddressType socksaddresstype, String s, int i) {
        super(SocksRequestType.CMD);
        if (sockscmdtype == null) {
            throw new NullPointerException("cmdType");
        } else if (socksaddresstype == null) {
            throw new NullPointerException("addressType");
        } else if (s == null) {
            throw new NullPointerException("host");
        } else {
            switch (socksaddresstype) {
                case IPv4:
                    if (!NetUtil.isValidIpV4Address(s)) {
                        throw new IllegalArgumentException(s + " is not a valid IPv4 address");
                    }
                    break;
                case DOMAIN:
                    if (IDN.toASCII(s).length() > 255) {
                        throw new IllegalArgumentException(s + " IDN: " + IDN.toASCII(s) + " exceeds 255 char limit");
                    }
                    break;
                case IPv6:
                    if (!NetUtil.isValidIpV6Address(s)) {
                        throw new IllegalArgumentException(s + " is not a valid IPv6 address");
                    }
                case UNKNOWN:
            }

            if (i < 0 && i >= 65535) {
                throw new IllegalArgumentException(i + " is not in bounds 0 < x < 65536");
            } else {
                this.cmdType = sockscmdtype;
                this.addressType = socksaddresstype;
                this.host = IDN.toASCII(s);
                this.port = i;
            }
        }
    }

    public SocksCmdType cmdType() {
        return this.cmdType;
    }

    public SocksAddressType addressType() {
        return this.addressType;
    }

    public String host() {
        return IDN.toUnicode(this.host);
    }

    public int port() {
        return this.port;
    }

    @Override
    public void encodeAsByteBuf(ByteBuf bytebuf) {
        bytebuf.writeByte(this.protocolVersion().byteValue());
        bytebuf.writeByte(this.cmdType.byteValue());
        bytebuf.writeByte(0);
        bytebuf.writeByte(this.addressType.byteValue());
        switch (this.addressType) {
            case IPv4:
                bytebuf.writeBytes(NetUtil.createByteArrayFromIpAddressString(this.host));
                bytebuf.writeShort(this.port);
                break;
            case DOMAIN:
                bytebuf.writeByte(this.host.length());
                bytebuf.writeBytes(this.host.getBytes(CharsetUtil.US_ASCII));
                bytebuf.writeShort(this.port);
                break;
            case IPv6:
                bytebuf.writeBytes(NetUtil.createByteArrayFromIpAddressString(this.host));
                bytebuf.writeShort(this.port);
        }

    }
}
