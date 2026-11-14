package net.minecraft.network.play.client;

import net.minecraft.network.PacketBuffer;

public class C03PacketPlayer$C06PacketPlayerPosLook extends C03PacketPlayer {

    public C03PacketPlayer$C06PacketPlayerPosLook() {
        this.field_149480_h = true;
        this.field_149481_i = true;
    }

    @Override
    public void func_148837_a(PacketBuffer packetbuffer) {
        this.field_149479_a = packetbuffer.readDouble();
        this.field_149477_b = packetbuffer.readDouble();
        this.field_149475_d = packetbuffer.readDouble();
        this.field_149478_c = packetbuffer.readDouble();
        this.field_149476_e = packetbuffer.readFloat();
        this.field_149473_f = packetbuffer.readFloat();
        super.func_148837_a(packetbuffer);
    }

    @Override
    public void func_148840_b(PacketBuffer packetbuffer) {
        packetbuffer.writeDouble(this.field_149479_a);
        packetbuffer.writeDouble(this.field_149477_b);
        packetbuffer.writeDouble(this.field_149475_d);
        packetbuffer.writeDouble(this.field_149478_c);
        packetbuffer.writeFloat(this.field_149476_e);
        packetbuffer.writeFloat(this.field_149473_f);
        super.func_148840_b(packetbuffer);
    }
}
