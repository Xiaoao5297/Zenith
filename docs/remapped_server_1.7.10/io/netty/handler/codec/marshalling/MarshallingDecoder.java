package io.netty.handler.codec.marshalling;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import org.jboss.marshalling.Unmarshaller;

public class MarshallingDecoder extends LengthFieldBasedFrameDecoder {

    private final UnmarshallerProvider provider;

    public MarshallingDecoder(UnmarshallerProvider unmarshallerprovider) {
        this(unmarshallerprovider, 1048576);
    }

    public MarshallingDecoder(UnmarshallerProvider unmarshallerprovider, int i) {
        super(i, 0, 4, 0, 4);
        this.provider = unmarshallerprovider;
    }

    @Override
    protected Object decode(ChannelHandlerContext channelhandlercontext, ByteBuf bytebuf) throws Exception {
        ByteBuf bytebuf1 = (ByteBuf) super.decode(channelhandlercontext, bytebuf);

        if (bytebuf1 == null) {
            return null;
        } else {
            Unmarshaller unmarshaller = this.provider.getUnmarshaller(channelhandlercontext);
            ChannelBufferByteInput channelbufferbyteinput = new ChannelBufferByteInput(bytebuf1);

            Object object;

            try {
                unmarshaller.start(channelbufferbyteinput);
                Object object1 = unmarshaller.readObject();

                unmarshaller.finish();
                object = object1;
            } finally {
                unmarshaller.close();
            }

            return object;
        }
    }

    @Override
    protected ByteBuf extractFrame(ChannelHandlerContext channelhandlercontext, ByteBuf bytebuf, int i, int j) {
        return bytebuf.slice(i, j);
    }
}
