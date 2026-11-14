package net.minecraft.util;

import com.google.common.collect.BiMap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import java.io.IOException;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.NetworkStatistics;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

public class MessageSerializer extends MessageToByteEncoder {

    private static final Logger field_150798_a = LogManager.getLogger();
    private static final Marker field_150797_b = MarkerManager.getMarker("PACKET_SENT", NetworkManager.field_150738_b);
    private final NetworkStatistics field_152500_c;

    public MessageSerializer(NetworkStatistics networkstatistics) {
        this.field_152500_c = networkstatistics;
    }

    protected void encode(ChannelHandlerContext channelhandlercontext, Packet packet, ByteBuf bytebuf) {
        Integer integer = (Integer) ((BiMap) channelhandlercontext.channel().attr(NetworkManager.field_150737_e).get()).inverse().get(packet.getClass());

        if (MessageSerializer.field_150798_a.isDebugEnabled()) {
            MessageSerializer.field_150798_a.debug(MessageSerializer.field_150797_b, "OUT: [{}:{}] {}[{}]", channelhandlercontext.channel().attr(NetworkManager.field_150739_c).get(), integer, packet.getClass().getName(), packet.func_148835_b());
        }

        if (integer == null) {
            throw new IOException("Can't serialize unregistered packet");
        } else {
            PacketBuffer packetbuffer = new PacketBuffer(bytebuf);

            packetbuffer.func_150787_b(integer);
            packet.func_148840_b(packetbuffer);
            this.field_152500_c.func_152464_b(integer, (long) packetbuffer.readableBytes());
        }
    }
}
