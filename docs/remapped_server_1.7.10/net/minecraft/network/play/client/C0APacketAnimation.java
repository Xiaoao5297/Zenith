package net.minecraft.network.play.client;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;

public class C0APacketAnimation extends Packet {

    private int field_149424_a;
    private int field_149423_b;

    public C0APacketAnimation() {}

    @Override
    public void func_148837_a(PacketBuffer packetbuffer) {
        this.field_149424_a = packetbuffer.readInt();
        this.field_149423_b = packetbuffer.readByte();
    }

    @Override
    public void func_148840_b(PacketBuffer packetbuffer) {
        packetbuffer.writeInt(this.field_149424_a);
        packetbuffer.writeByte(this.field_149423_b);
    }

    public void func_148833_a(INetHandlerPlayServer inethandlerplayserver) {
        inethandlerplayserver.func_147350_a(this);
    }

    @Override
    public String func_148835_b() {
        return String.format("id=%d, type=%d", this.field_149424_a, this.field_149423_b);
    }

    public int func_149421_d() {
        return this.field_149423_b;
    }
}
