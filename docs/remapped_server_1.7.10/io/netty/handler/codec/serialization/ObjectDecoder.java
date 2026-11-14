package io.netty.handler.codec.serialization;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

public class ObjectDecoder extends LengthFieldBasedFrameDecoder {

    private final ClassResolver classResolver;

    public ObjectDecoder(ClassResolver classresolver) {
        this(1048576, classresolver);
    }

    public ObjectDecoder(int i, ClassResolver classresolver) {
        super(i, 0, 4, 0, 4);
        this.classResolver = classresolver;
    }

    @Override
    protected Object decode(ChannelHandlerContext channelhandlercontext, ByteBuf bytebuf) throws Exception {
        ByteBuf bytebuf1 = (ByteBuf) super.decode(channelhandlercontext, bytebuf);

        return bytebuf1 == null ? null : (new CompactObjectInputStream(new ByteBufInputStream(bytebuf1), this.classResolver)).readObject();
    }

    @Override
    protected ByteBuf extractFrame(ChannelHandlerContext channelhandlercontext, ByteBuf bytebuf, int i, int j) {
        return bytebuf.slice(i, j);
    }
}
