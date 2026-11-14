package net.minecraft.network.play.server;

import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S0BPacketAnimation extends Packet {

    private int field_148981_a;
    private int field_148980_b;

    public S0BPacketAnimation() {}

    public S0BPacketAnimation(Entity entity, int i) {
        this.field_148981_a = entity.func_145782_y();
        this.field_148980_b = i;
    }

    @Override
    public void func_148837_a(PacketBuffer packetbuffer) {
        this.field_148981_a = packetbuffer.func_150792_a();
        this.field_148980_b = packetbuffer.readUnsignedByte();
    }

    @Override
    public void func_148840_b(PacketBuffer packetbuffer) {
        packetbuffer.func_150787_b(this.field_148981_a);
        packetbuffer.writeByte(this.field_148980_b);
    }

    public void func_148833_a(INetHandlerPlayClient inethandlerplayclient) {
        inethandlerplayclient.func_147279_a(this);
    }

    @Override
    public String func_148835_b() {
        return String.format("id=%d, type=%d", this.field_148981_a, this.field_148980_b);
    }
}
