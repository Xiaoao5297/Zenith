package io.netty.handler.codec.socks;

import io.netty.buffer.ByteBuf;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public final class SocksInitRequest extends SocksRequest {

    private final List<SocksAuthScheme> authSchemes;

    public SocksInitRequest(List<SocksAuthScheme> list) {
        super(SocksRequestType.INIT);
        if (list == null) {
            throw new NullPointerException("authSchemes");
        } else {
            this.authSchemes = list;
        }
    }

    public List<SocksAuthScheme> authSchemes() {
        return Collections.unmodifiableList(this.authSchemes);
    }

    @Override
    public void encodeAsByteBuf(ByteBuf bytebuf) {
        bytebuf.writeByte(this.protocolVersion().byteValue());
        bytebuf.writeByte(this.authSchemes.size());
        Iterator iterator = this.authSchemes.iterator();

        while (iterator.hasNext()) {
            SocksAuthScheme socksauthscheme = (SocksAuthScheme) iterator.next();

            bytebuf.writeByte(socksauthscheme.byteValue());
        }

    }
}
