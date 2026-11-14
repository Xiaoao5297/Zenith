package io.netty.handler.codec.socks;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import java.util.ArrayList;
import java.util.List;

public class SocksInitRequestDecoder extends ReplayingDecoder<SocksInitRequestDecoder.State> {

    private static final String name = "SOCKS_INIT_REQUEST_DECODER";
    private final List<SocksAuthScheme> authSchemes = new ArrayList();
    private SocksProtocolVersion version;
    private byte authSchemeNum;
    private SocksRequest msg;

    public static String getName() {
        return "SOCKS_INIT_REQUEST_DECODER";
    }

    public SocksInitRequestDecoder() {
        super(SocksInitRequestDecoder.State.CHECK_PROTOCOL_VERSION);
        this.msg = SocksCommonUtils.UNKNOWN_SOCKS_REQUEST;
    }

    @Override
    protected void decode(ChannelHandlerContext channelhandlercontext, ByteBuf bytebuf, List<Object> list) throws Exception {
        switch ((SocksInitRequestDecoder.State) this.state()) {
            case CHECK_PROTOCOL_VERSION:
                this.version = SocksProtocolVersion.fromByte(bytebuf.readByte());
                if (this.version != SocksProtocolVersion.SOCKS5) {
                    break;
                }

                this.checkpoint(SocksInitRequestDecoder.State.READ_AUTH_SCHEMES);
            case READ_AUTH_SCHEMES:
                this.authSchemes.clear();
                this.authSchemeNum = bytebuf.readByte();

                for (int i = 0; i < this.authSchemeNum; ++i) {
                    this.authSchemes.add(SocksAuthScheme.fromByte(bytebuf.readByte()));
                }

                this.msg = new SocksInitRequest(this.authSchemes);
        }

        channelhandlercontext.pipeline().remove((ChannelHandler) this);
        list.add(this.msg);
    }

    static enum State {

        CHECK_PROTOCOL_VERSION, READ_AUTH_SCHEMES;

        private State() {}
    }
}
