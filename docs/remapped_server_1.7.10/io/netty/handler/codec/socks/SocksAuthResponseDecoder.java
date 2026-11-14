package io.netty.handler.codec.socks;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import java.util.List;

public class SocksAuthResponseDecoder extends ReplayingDecoder<SocksAuthResponseDecoder.State> {

    private static final String name = "SOCKS_AUTH_RESPONSE_DECODER";
    private SocksSubnegotiationVersion version;
    private SocksAuthStatus authStatus;
    private SocksResponse msg;

    public static String getName() {
        return "SOCKS_AUTH_RESPONSE_DECODER";
    }

    public SocksAuthResponseDecoder() {
        super(SocksAuthResponseDecoder.State.CHECK_PROTOCOL_VERSION);
        this.msg = SocksCommonUtils.UNKNOWN_SOCKS_RESPONSE;
    }

    @Override
    protected void decode(ChannelHandlerContext channelhandlercontext, ByteBuf bytebuf, List<Object> list) throws Exception {
        switch ((SocksAuthResponseDecoder.State) this.state()) {
            case CHECK_PROTOCOL_VERSION:
                this.version = SocksSubnegotiationVersion.fromByte(bytebuf.readByte());
                if (this.version != SocksSubnegotiationVersion.AUTH_PASSWORD) {
                    break;
                }

                this.checkpoint(SocksAuthResponseDecoder.State.READ_AUTH_RESPONSE);
            case READ_AUTH_RESPONSE:
                this.authStatus = SocksAuthStatus.fromByte(bytebuf.readByte());
                this.msg = new SocksAuthResponse(this.authStatus);
        }

        channelhandlercontext.pipeline().remove((ChannelHandler) this);
        list.add(this.msg);
    }

    static enum State {

        CHECK_PROTOCOL_VERSION, READ_AUTH_RESPONSE;

        private State() {}
    }
}
