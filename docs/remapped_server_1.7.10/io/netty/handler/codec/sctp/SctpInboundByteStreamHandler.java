package io.netty.handler.codec.sctp;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.sctp.SctpMessage;
import io.netty.handler.codec.CodecException;
import io.netty.handler.codec.MessageToMessageDecoder;
import java.util.List;

public class SctpInboundByteStreamHandler extends MessageToMessageDecoder<SctpMessage> {

    private final int protocolIdentifier;
    private final int streamIdentifier;

    public SctpInboundByteStreamHandler(int i, int j) {
        this.protocolIdentifier = i;
        this.streamIdentifier = j;
    }

    @Override
    public final boolean acceptInboundMessage(Object object) throws Exception {
        return super.acceptInboundMessage(object) ? this.acceptInboundMessage((SctpMessage) object) : false;
    }

    protected boolean acceptInboundMessage(SctpMessage sctpmessage) {
        return sctpmessage.protocolIdentifier() == this.protocolIdentifier && sctpmessage.streamIdentifier() == this.streamIdentifier;
    }

    protected void decode(ChannelHandlerContext channelhandlercontext, SctpMessage sctpmessage, List<Object> list) throws Exception {
        if (!sctpmessage.isComplete()) {
            throw new CodecException(String.format("Received SctpMessage is not complete, please add %s in the pipeline before this handler", SctpMessageCompletionHandler.class.getSimpleName()));
        } else {
            list.add(sctpmessage.content().retain());
        }
    }
}
