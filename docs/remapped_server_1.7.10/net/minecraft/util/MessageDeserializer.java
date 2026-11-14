package net.minecraft.util;

import com.google.common.collect.BiMap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import java.io.IOException;
import java.util.List;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.NetworkStatistics;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

public class MessageDeserializer extends ByteToMessageDecoder {

    private static final Logger field_150800_a = LogManager.getLogger();
    private static final Marker field_150799_b = MarkerManager.getMarker("PACKET_RECEIVED", NetworkManager.field_150738_b);
    private final NetworkStatistics field_152499_c;

    public MessageDeserializer(NetworkStatistics networkstatistics) {
        this.field_152499_c = networkstatistics;
    }

    @Override
    protected void decode(ChannelHandlerContext channelhandlercontext, ByteBuf bytebuf, List list) {
        int i = bytebuf.readableBytes();

        if (i != 0) {
            PacketBuffer packetbuffer = new PacketBuffer(bytebuf);
            int j = packetbuffer.func_150792_a();
            Packet packet = Packet.func_148839_a((BiMap) channelhandlercontext.channel().attr(NetworkManager.field_150736_d).get(), j);

            if (packet == null) {
                throw new IOException("Bad packet id " + j);
            } else {
                packet.func_148837_a(packetbuffer);
                if (packetbuffer.readableBytes() > 0) {
                    throw new IOException("Packet was larger than I expected, found " + packetbuffer.readableBytes() + " bytes extra whilst reading packet " + j);
                } else {
                    list.add(packet);
                    this.field_152499_c.func_152469_a(j, (long) i);
                    if (MessageDeserializer.field_150800_a.isDebugEnabled()) {
                        MessageDeserializer.field_150800_a.debug(MessageDeserializer.field_150799_b, " IN: [{}:{}] {}[{}]", channelhandlercontext.channel().attr(NetworkManager.field_150739_c).get(), j, packet.getClass().getName(), packet.func_148835_b());
                    }

                }
            }
        }
    }
}
