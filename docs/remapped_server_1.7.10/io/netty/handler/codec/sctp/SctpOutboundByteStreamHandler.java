package io.netty.handler.codec.sctp;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.sctp.SctpMessage;
import io.netty.handler.codec.MessageToMessageEncoder;
import java.util.List;

public class SctpOutboundByteStreamHandler extends MessageToMessageEncoder<ByteBuf> {

    private final int streamIdentifier;
    private final int protocolIdentifier;

    public SctpOutboundByteStreamHandler(int i, int j) {
        this.streamIdentifier = i;
        this.protocolIdentifier = j;
    }

    protected void encode(ChannelHandlerContext channelhandlercontext, ByteBuf bytebuf, List<Object> list) throws Exception {
        list.add(new SctpMessage(this.streamIdentifier, this.protocolIdentifier, bytebuf.retain()));
    }
}
