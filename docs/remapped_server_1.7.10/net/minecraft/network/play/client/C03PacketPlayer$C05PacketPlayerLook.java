package net.minecraft.network.play.client;

import net.minecraft.network.PacketBuffer;

public class C03PacketPlayer$C05PacketPlayerLook extends C03PacketPlayer {

    public C03PacketPlayer$C05PacketPlayerLook() {
        this.field_149481_i = true;
    }

    @Override
    public void func_148837_a(PacketBuffer packetbuffer) {
        this.field_149476_e = packetbuffer.readFloat();
        this.field_149473_f = packetbuffer.readFloat();
        super.func_148837_a(packetbuffer);
    }

    @Override
    public void func_148840_b(PacketBuffer packetbuffer) {
        packetbuffer.writeFloat(this.field_149476_e);
        packetbuffer.writeFloat(this.field_149473_f);
        super.func_148840_b(packetbuffer);
    }
}
