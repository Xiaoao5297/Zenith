package io.netty.handler.codec.marshalling;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.jboss.marshalling.Marshaller;

@ChannelHandler.Sharable
public class CompatibleMarshallingEncoder extends MessageToByteEncoder<Object> {

    private final MarshallerProvider provider;

    public CompatibleMarshallingEncoder(MarshallerProvider marshallerprovider) {
        this.provider = marshallerprovider;
    }

    @Override
    protected void encode(ChannelHandlerContext channelhandlercontext, Object object, ByteBuf bytebuf) throws Exception {
        Marshaller marshaller = this.provider.getMarshaller(channelhandlercontext);

        marshaller.start(new ChannelBufferByteOutput(bytebuf));
        marshaller.writeObject(object);
        marshaller.finish();
        marshaller.close();
    }
}
