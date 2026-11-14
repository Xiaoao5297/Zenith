package net.minecraft.network.play.server;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S3FPacketCustomPayload extends Packet {

    private String field_149172_a;
    private byte[] field_149171_b;

    public S3FPacketCustomPayload() {}

    public S3FPacketCustomPayload(String s, ByteBuf bytebuf) {
        this(s, bytebuf.array());
    }

    public S3FPacketCustomPayload(String s, byte[] abyte) {
        this.field_149172_a = s;
        this.field_149171_b = abyte;
        if (abyte.length >= 1048576) {
            throw new IllegalArgumentException("Payload may not be larger than 1048576 bytes");
        }
    }

    @Override
    public void func_148837_a(PacketBuffer packetbuffer) {
        this.field_149172_a = packetbuffer.func_150789_c(20);
        this.field_149171_b = new byte[packetbuffer.readUnsignedShort()];
        packetbuffer.readBytes(this.field_149171_b);
    }

    @Override
    public void func_148840_b(PacketBuffer packetbuffer) {
        packetbuffer.func_150785_a(this.field_149172_a);
        packetbuffer.writeShort(this.field_149171_b.length);
        packetbuffer.writeBytes(this.field_149171_b);
    }

    public void func_148833_a(INetHandlerPlayClient inethandlerplayclient) {
        inethandlerplayclient.func_147240_a(this);
    }
}
