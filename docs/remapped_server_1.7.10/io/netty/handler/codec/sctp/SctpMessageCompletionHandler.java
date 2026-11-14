package io.netty.handler.codec.sctp;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.sctp.SctpMessage;
import io.netty.handler.codec.MessageToMessageDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SctpMessageCompletionHandler extends MessageToMessageDecoder<SctpMessage> {

    private final Map<Integer, ByteBuf> fragments = new HashMap();

    public SctpMessageCompletionHandler() {}

    protected void decode(ChannelHandlerContext channelhandlercontext, SctpMessage sctpmessage, List<Object> list) throws Exception {
        ByteBuf bytebuf = sctpmessage.content();
        int i = sctpmessage.protocolIdentifier();
        int j = sctpmessage.streamIdentifier();
        boolean flag = sctpmessage.isComplete();
        ByteBuf bytebuf1;

        if (this.fragments.containsKey(j)) {
            bytebuf1 = (ByteBuf) this.fragments.remove(j);
        } else {
            bytebuf1 = Unpooled.EMPTY_BUFFER;
        }

        if (flag && !bytebuf1.isReadable()) {
            list.add(sctpmessage);
        } else if (!flag && bytebuf1.isReadable()) {
            this.fragments.put(j, Unpooled.wrappedBuffer(bytebuf1, bytebuf));
        } else if (flag && bytebuf1.isReadable()) {
            this.fragments.remove(j);
            SctpMessage sctpmessage1 = new SctpMessage(i, j, Unpooled.wrappedBuffer(bytebuf1, bytebuf));

            list.add(sctpmessage1);
        } else {
            this.fragments.put(j, bytebuf);
        }

        bytebuf.retain();
    }
}
