package net.minecraft.network.play.server;

import net.minecraft.network.PacketBuffer;

public class S14PacketEntity$S16PacketEntityLook extends S14PacketEntity {

    public S14PacketEntity$S16PacketEntityLook() {
        this.field_149069_g = true;
    }

    public S14PacketEntity$S16PacketEntityLook(int i, byte b0, byte b1) {
        super(i);
        this.field_149071_e = b0;
        this.field_149068_f = b1;
        this.field_149069_g = true;
    }

    @Override
    public void func_148837_a(PacketBuffer packetbuffer) {
        super.func_148837_a(packetbuffer);
        this.field_149071_e = packetbuffer.readByte();
        this.field_149068_f = packetbuffer.readByte();
    }

    @Override
    public void func_148840_b(PacketBuffer packetbuffer) {
        super.func_148840_b(packetbuffer);
        packetbuffer.writeByte(this.field_149071_e);
        packetbuffer.writeByte(this.field_149068_f);
    }

    @Override
    public String func_148835_b() {
        return super.func_148835_b() + String.format(", yRot=%d, xRot=%d", this.field_149071_e, this.field_149068_f);
    }
}
