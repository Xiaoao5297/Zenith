package net.minecraft.network.play.client;

import net.minecraft.network.PacketBuffer;

public class C03PacketPlayer$C04PacketPlayerPosition extends C03PacketPlayer {

    public C03PacketPlayer$C04PacketPlayerPosition() {
        this.field_149480_h = true;
    }

    @Override
    public void func_148837_a(PacketBuffer packetbuffer) {
        this.field_149479_a = packetbuffer.readDouble();
        this.field_149477_b = packetbuffer.readDouble();
        this.field_149475_d = packetbuffer.readDouble();
        this.field_149478_c = packetbuffer.readDouble();
        super.func_148837_a(packetbuffer);
    }

    @Override
    public void func_148840_b(PacketBuffer packetbuffer) {
        packetbuffer.writeDouble(this.field_149479_a);
        packetbuffer.writeDouble(this.field_149477_b);
        packetbuffer.writeDouble(this.field_149475_d);
        packetbuffer.writeDouble(this.field_149478_c);
        super.func_148840_b(packetbuffer);
    }
}
