package net.minecraft.network.play.server;

import net.minecraft.network.PacketBuffer;

public class S14PacketEntity$S15PacketEntityRelMove extends S14PacketEntity {

    public S14PacketEntity$S15PacketEntityRelMove() {}

    public S14PacketEntity$S15PacketEntityRelMove(int i, byte b0, byte b1, byte b2) {
        super(i);
        this.field_149072_b = b0;
        this.field_149073_c = b1;
        this.field_149070_d = b2;
    }

    @Override
    public void func_148837_a(PacketBuffer packetbuffer) {
        super.func_148837_a(packetbuffer);
        this.field_149072_b = packetbuffer.readByte();
        this.field_149073_c = packetbuffer.readByte();
        this.field_149070_d = packetbuffer.readByte();
    }

    @Override
    public void func_148840_b(PacketBuffer packetbuffer) {
        super.func_148840_b(packetbuffer);
        packetbuffer.writeByte(this.field_149072_b);
        packetbuffer.writeByte(this.field_149073_c);
        packetbuffer.writeByte(this.field_149070_d);
    }

    @Override
    public String func_148835_b() {
        return super.func_148835_b() + String.format(", xa=%d, ya=%d, za=%d", this.field_149072_b, this.field_149073_c, this.field_149070_d);
    }
}
