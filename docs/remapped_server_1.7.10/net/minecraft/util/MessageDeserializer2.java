package net.minecraft.util;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.CorruptedFrameException;
import java.util.List;
import net.minecraft.network.PacketBuffer;

public class MessageDeserializer2 extends ByteToMessageDecoder {

    public MessageDeserializer2() {}

    @Override
    protected void decode(ChannelHandlerContext channelhandlercontext, ByteBuf bytebuf, List list) {
        bytebuf.markReaderIndex();
        byte[] abyte = new byte[3];

        for (int i = 0; i < abyte.length; ++i) {
            if (!bytebuf.isReadable()) {
                bytebuf.resetReaderIndex();
                return;
            }

            abyte[i] = bytebuf.readByte();
            if (abyte[i] >= 0) {
                PacketBuffer packetbuffer = new PacketBuffer(Unpooled.wrappedBuffer(abyte));

                try {
                    int j = packetbuffer.func_150792_a();

                    if (bytebuf.readableBytes() >= j) {
                        list.add(bytebuf.readBytes(j));
                        return;
                    }

                    bytebuf.resetReaderIndex();
                } finally {
                    packetbuffer.release();
                }

                return;
            }
        }

        throw new CorruptedFrameException("length wider than 21-bit");
    }
}
