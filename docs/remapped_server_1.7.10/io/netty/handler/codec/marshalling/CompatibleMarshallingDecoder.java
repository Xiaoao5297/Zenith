package io.netty.handler.codec.marshalling;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import io.netty.handler.codec.TooLongFrameException;
import java.util.List;
import org.jboss.marshalling.ByteInput;
import org.jboss.marshalling.Unmarshaller;

public class CompatibleMarshallingDecoder extends ReplayingDecoder<Void> {

    protected final UnmarshallerProvider provider;
    protected final int maxObjectSize;
    private boolean discardingTooLongFrame;

    public CompatibleMarshallingDecoder(UnmarshallerProvider unmarshallerprovider, int i) {
        this.provider = unmarshallerprovider;
        this.maxObjectSize = i;
    }

    @Override
    protected void decode(ChannelHandlerContext channelhandlercontext, ByteBuf bytebuf, List<Object> list) throws Exception {
        if (this.discardingTooLongFrame) {
            bytebuf.skipBytes(this.actualReadableBytes());
            this.checkpoint();
        } else {
            Unmarshaller unmarshaller = this.provider.getUnmarshaller(channelhandlercontext);
            Object object = new ChannelBufferByteInput(bytebuf);

            if (this.maxObjectSize != Integer.MAX_VALUE) {
                object = new LimitingByteInput((ByteInput) object, (long) this.maxObjectSize);
            }

            try {
                unmarshaller.start((ByteInput) object);
                Object object1 = unmarshaller.readObject();

                unmarshaller.finish();
                list.add(object1);
            } catch (LimitingByteInput.TooBigObjectException limitingbyteinput_toobigobjectexception) {
                this.discardingTooLongFrame = true;
                throw new TooLongFrameException();
            } finally {
                unmarshaller.close();
            }

        }
    }

    @Override
    protected void decodeLast(ChannelHandlerContext channelhandlercontext, ByteBuf bytebuf, List<Object> list) throws Exception {
        switch (bytebuf.readableBytes()) {
            case 0:
                return;
            case 1:
                if (bytebuf.getByte(bytebuf.readerIndex()) == 121) {
                    bytebuf.skipBytes(1);
                    return;
                }
            default:
                this.decode(channelhandlercontext, bytebuf, list);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext channelhandlercontext, Throwable throwable) throws Exception {
        if (throwable instanceof TooLongFrameException) {
            channelhandlercontext.close();
        } else {
            super.exceptionCaught(channelhandlercontext, throwable);
        }

    }
}
