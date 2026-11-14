package io.netty.handler.codec.socks;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

@ChannelHandler.Sharable
public class SocksMessageEncoder extends MessageToByteEncoder<SocksMessage> {

    private static final String name = "SOCKS_MESSAGE_ENCODER";

    public SocksMessageEncoder() {}

    public static String getName() {
        return "SOCKS_MESSAGE_ENCODER";
    }

    protected void encode(ChannelHandlerContext channelhandlercontext, SocksMessage socksmessage, ByteBuf bytebuf) throws Exception {
        socksmessage.encodeAsByteBuf(bytebuf);
    }
}
