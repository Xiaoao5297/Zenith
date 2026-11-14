package net.minecraft.network.play.server;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S34PacketMaps extends Packet {

    private int field_149191_a;
    private byte[] field_149190_b;

    public S34PacketMaps() {}

    public S34PacketMaps(int i, byte[] abyte) {
        this.field_149191_a = i;
        this.field_149190_b = abyte;
    }

    @Override
    public void func_148837_a(PacketBuffer packetbuffer) {
        this.field_149191_a = packetbuffer.func_150792_a();
        this.field_149190_b = new byte[packetbuffer.readUnsignedShort()];
        packetbuffer.readBytes(this.field_149190_b);
    }

    @Override
    public void func_148840_b(PacketBuffer packetbuffer) {
        packetbuffer.func_150787_b(this.field_149191_a);
        packetbuffer.writeShort(this.field_149190_b.length);
        packetbuffer.writeBytes(this.field_149190_b);
    }

    public void func_148833_a(INetHandlerPlayClient inethandlerplayclient) {
        inethandlerplayclient.func_147264_a(this);
    }

    @Override
    public String func_148835_b() {
        return String.format("id=%d, length=%d", this.field_149191_a, this.field_149190_b.length);
    }
}
