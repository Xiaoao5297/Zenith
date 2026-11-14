package net.minecraft.util;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import net.minecraft.network.PacketBuffer;

public class MessageSerializer2 extends MessageToByteEncoder {

    public MessageSerializer2() {}

    protected void encode(ChannelHandlerContext channelhandlercontext, ByteBuf bytebuf, ByteBuf bytebuf1) {
        int i = bytebuf.readableBytes();
        int j = PacketBuffer.func_150790_a(i);

        if (j > 3) {
            throw new IllegalArgumentException("unable to fit " + i + " into " + 3);
        } else {
            PacketBuffer packetbuffer = new PacketBuffer(bytebuf1);

            packetbuffer.ensureWritable(j + i);
            packetbuffer.func_150787_b(i);
            packetbuffer.writeBytes(bytebuf, bytebuf.readerIndex(), i);
        }
    }
}
