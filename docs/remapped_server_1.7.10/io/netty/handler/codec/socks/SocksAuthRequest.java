package io.netty.handler.codec.socks;

import io.netty.buffer.ByteBuf;
import io.netty.util.CharsetUtil;
import java.nio.charset.CharsetEncoder;

public final class SocksAuthRequest extends SocksRequest {

    private static final CharsetEncoder asciiEncoder = CharsetUtil.getEncoder(CharsetUtil.US_ASCII);
    private static final SocksSubnegotiationVersion SUBNEGOTIATION_VERSION = SocksSubnegotiationVersion.AUTH_PASSWORD;
    private final String username;
    private final String password;

    public SocksAuthRequest(String s, String s1) {
        super(SocksRequestType.AUTH);
        if (s == null) {
            throw new NullPointerException("username");
        } else if (s1 == null) {
            throw new NullPointerException("username");
        } else if (SocksAuthRequest.asciiEncoder.canEncode(s) && SocksAuthRequest.asciiEncoder.canEncode(s1)) {
            if (s.length() > 255) {
                throw new IllegalArgumentException(s + " exceeds 255 char limit");
            } else if (s1.length() > 255) {
                throw new IllegalArgumentException(s1 + " exceeds 255 char limit");
            } else {
                this.username = s;
                this.password = s1;
            }
        } else {
            throw new IllegalArgumentException(" username: " + s + " or password: " + s1 + " values should be in pure ascii");
        }
    }

    public String username() {
        return this.username;
    }

    public String password() {
        return this.password;
    }

    @Override
    public void encodeAsByteBuf(ByteBuf bytebuf) {
        bytebuf.writeByte(SocksAuthRequest.SUBNEGOTIATION_VERSION.byteValue());
        bytebuf.writeByte(this.username.length());
        bytebuf.writeBytes(this.username.getBytes(CharsetUtil.US_ASCII));
        bytebuf.writeByte(this.password.length());
        bytebuf.writeBytes(this.password.getBytes(CharsetUtil.US_ASCII));
    }
}
