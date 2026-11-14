package io.netty.handler.codec.serialization;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import java.io.Serializable;

@ChannelHandler.Sharable
public class ObjectEncoder extends MessageToByteEncoder<Serializable> {

    private static final byte[] LENGTH_PLACEHOLDER = new byte[4];

    public ObjectEncoder() {}

    protected void encode(ChannelHandlerContext channelhandlercontext, Serializable serializable, ByteBuf bytebuf) throws Exception {
        int i = bytebuf.writerIndex();
        ByteBufOutputStream bytebufoutputstream = new ByteBufOutputStream(bytebuf);

        bytebufoutputstream.write(ObjectEncoder.LENGTH_PLACEHOLDER);
        CompactObjectOutputStream compactobjectoutputstream = new CompactObjectOutputStream(bytebufoutputstream);

        compactobjectoutputstream.writeObject(serializable);
        compactobjectoutputstream.flush();
        compactobjectoutputstream.close();
        int j = bytebuf.writerIndex();

        bytebuf.setInt(i, j - i - 4);
    }
}
