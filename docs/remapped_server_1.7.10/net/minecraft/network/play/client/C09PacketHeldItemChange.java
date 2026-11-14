package net.minecraft.network.play.client;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;

public class C09PacketHeldItemChange extends Packet {

    private int field_149615_a;

    public C09PacketHeldItemChange() {}

    @Override
    public void func_148837_a(PacketBuffer packetbuffer) {
        this.field_149615_a = packetbuffer.readShort();
    }

    @Override
    public void func_148840_b(PacketBuffer packetbuffer) {
        packetbuffer.writeShort(this.field_149615_a);
    }

    public void func_148833_a(INetHandlerPlayServer inethandlerplayserver) {
        inethandlerplayserver.func_147355_a(this);
    }

    public int func_149614_c() {
        return this.field_149615_a;
    }
}
