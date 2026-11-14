package io.netty.handler.codec.marshalling;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.jboss.marshalling.Marshaller;

@ChannelHandler.Sharable
public class MarshallingEncoder extends MessageToByteEncoder<Object> {

    private static final byte[] LENGTH_PLACEHOLDER = new byte[4];
    private final MarshallerProvider provider;

    public MarshallingEncoder(MarshallerProvider marshallerprovider) {
        this.provider = marshallerprovider;
    }

    @Override
    protected void encode(ChannelHandlerContext channelhandlercontext, Object object, ByteBuf bytebuf) throws Exception {
        Marshaller marshaller = this.provider.getMarshaller(channelhandlercontext);
        int i = bytebuf.writerIndex();

        bytebuf.writeBytes(MarshallingEncoder.LENGTH_PLACEHOLDER);
        ChannelBufferByteOutput channelbufferbyteoutput = new ChannelBufferByteOutput(bytebuf);

        marshaller.start(channelbufferbyteoutput);
        marshaller.writeObject(object);
        marshaller.finish();
        marshaller.close();
        bytebuf.setInt(i, bytebuf.writerIndex() - i - 4);
    }
}
