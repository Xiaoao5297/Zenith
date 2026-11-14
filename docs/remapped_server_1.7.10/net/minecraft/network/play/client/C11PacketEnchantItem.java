package net.minecraft.network.play.client;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;

public class C11PacketEnchantItem extends Packet {

    private int field_149541_a;
    private int field_149540_b;

    public C11PacketEnchantItem() {}

    public void func_148833_a(INetHandlerPlayServer inethandlerplayserver) {
        inethandlerplayserver.func_147338_a(this);
    }

    @Override
    public void func_148837_a(PacketBuffer packetbuffer) {
        this.field_149541_a = packetbuffer.readByte();
        this.field_149540_b = packetbuffer.readByte();
    }

    @Override
    public void func_148840_b(PacketBuffer packetbuffer) {
        packetbuffer.writeByte(this.field_149541_a);
        packetbuffer.writeByte(this.field_149540_b);
    }

    @Override
    public String func_148835_b() {
        return String.format("id=%d, button=%d", this.field_149541_a, this.field_149540_b);
    }

    public int func_149539_c() {
        return this.field_149541_a;
    }

    public int func_149537_d() {
        return this.field_149540_b;
    }
}
