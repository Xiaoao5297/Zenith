package net.minecraft.network.play.server;

import net.minecraft.network.PacketBuffer;

public class S14PacketEntity$S17PacketEntityLookMove extends S14PacketEntity {

    public S14PacketEntity$S17PacketEntityLookMove() {
        this.field_149069_g = true;
    }

    public S14PacketEntity$S17PacketEntityLookMove(int i, byte b0, byte b1, byte b2, byte b3, byte b4) {
        super(i);
        this.field_149072_b = b0;
        this.field_149073_c = b1;
        this.field_149070_d = b2;
        this.field_149071_e = b3;
        this.field_149068_f = b4;
        this.field_149069_g = true;
    }

    @Override
    public void func_148837_a(PacketBuffer packetbuffer) {
        super.func_148837_a(packetbuffer);
        this.field_149072_b = packetbuffer.readByte();
        this.field_149073_c = packetbuffer.readByte();
        this.field_149070_d = packetbuffer.readByte();
        this.field_149071_e = packetbuffer.readByte();
        this.field_149068_f = packetbuffer.readByte();
    }

    @Override
    public void func_148840_b(PacketBuffer packetbuffer) {
        super.func_148840_b(packetbuffer);
        packetbuffer.writeByte(this.field_149072_b);
        packetbuffer.writeByte(this.field_149073_c);
        packetbuffer.writeByte(this.field_149070_d);
        packetbuffer.writeByte(this.field_149071_e);
        packetbuffer.writeByte(this.field_149068_f);
    }

    @Override
    public String func_148835_b() {
        return super.func_148835_b() + String.format(", xa=%d, ya=%d, za=%d, yRot=%d, xRot=%d", this.field_149072_b, this.field_149073_c, this.field_149070_d, this.field_149071_e, this.field_149068_f);
    }
}
