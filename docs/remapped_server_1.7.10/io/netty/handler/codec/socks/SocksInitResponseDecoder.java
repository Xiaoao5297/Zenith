package io.netty.handler.codec.socks;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import java.util.List;

public class SocksInitResponseDecoder extends ReplayingDecoder<SocksInitResponseDecoder.State> {

    private static final String name = "SOCKS_INIT_RESPONSE_DECODER";
    private SocksProtocolVersion version;
    private SocksAuthScheme authScheme;
    private SocksResponse msg;

    public static String getName() {
        return "SOCKS_INIT_RESPONSE_DECODER";
    }

    public SocksInitResponseDecoder() {
        super(SocksInitResponseDecoder.State.CHECK_PROTOCOL_VERSION);
        this.msg = SocksCommonUtils.UNKNOWN_SOCKS_RESPONSE;
    }

    @Override
    protected void decode(ChannelHandlerContext channelhandlercontext, ByteBuf bytebuf, List<Object> list) throws Exception {
        switch ((SocksInitResponseDecoder.State) this.state()) {
            case CHECK_PROTOCOL_VERSION:
                this.version = SocksProtocolVersion.fromByte(bytebuf.readByte());
                if (this.version != SocksProtocolVersion.SOCKS5) {
                    break;
                }

                this.checkpoint(SocksInitResponseDecoder.State.READ_PREFFERED_AUTH_TYPE);
            case READ_PREFFERED_AUTH_TYPE:
                this.authScheme = SocksAuthScheme.fromByte(bytebuf.readByte());
                this.msg = new SocksInitResponse(this.authScheme);
        }

        channelhandlercontext.pipeline().remove((ChannelHandler) this);
        list.add(this.msg);
    }

    static enum State {

        CHECK_PROTOCOL_VERSION, READ_PREFFERED_AUTH_TYPE;

        private State() {}
    }
}
