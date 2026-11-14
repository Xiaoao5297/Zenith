package io.netty.handler.codec.socks;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import io.netty.util.CharsetUtil;
import java.util.List;

public class SocksAuthRequestDecoder extends ReplayingDecoder<SocksAuthRequestDecoder.State> {

    private static final String name = "SOCKS_AUTH_REQUEST_DECODER";
    private SocksSubnegotiationVersion version;
    private int fieldLength;
    private String username;
    private String password;
    private SocksRequest msg;

    public static String getName() {
        return "SOCKS_AUTH_REQUEST_DECODER";
    }

    public SocksAuthRequestDecoder() {
        super(SocksAuthRequestDecoder.State.CHECK_PROTOCOL_VERSION);
        this.msg = SocksCommonUtils.UNKNOWN_SOCKS_REQUEST;
    }

    @Override
    protected void decode(ChannelHandlerContext channelhandlercontext, ByteBuf bytebuf, List<Object> list) throws Exception {
        switch ((SocksAuthRequestDecoder.State) this.state()) {
            case CHECK_PROTOCOL_VERSION:
                this.version = SocksSubnegotiationVersion.fromByte(bytebuf.readByte());
                if (this.version != SocksSubnegotiationVersion.AUTH_PASSWORD) {
                    break;
                }

                this.checkpoint(SocksAuthRequestDecoder.State.READ_USERNAME);
            case READ_USERNAME:
                this.fieldLength = bytebuf.readByte();
                this.username = bytebuf.readBytes(this.fieldLength).toString(CharsetUtil.US_ASCII);
                this.checkpoint(SocksAuthRequestDecoder.State.READ_PASSWORD);
            case READ_PASSWORD:
                this.fieldLength = bytebuf.readByte();
                this.password = bytebuf.readBytes(this.fieldLength).toString(CharsetUtil.US_ASCII);
                this.msg = new SocksAuthRequest(this.username, this.password);
        }

        channelhandlercontext.pipeline().remove((ChannelHandler) this);
        list.add(this.msg);
    }

    static enum State {

        CHECK_PROTOCOL_VERSION, READ_USERNAME, READ_PASSWORD;

        private State() {}
    }
}
