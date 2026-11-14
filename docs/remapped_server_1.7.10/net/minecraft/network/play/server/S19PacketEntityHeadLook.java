package net.minecraft.network.play.server;

import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S19PacketEntityHeadLook extends Packet {

    private int field_149384_a;
    private byte field_149383_b;

    public S19PacketEntityHeadLook() {}

    public S19PacketEntityHeadLook(Entity entity, byte b0) {
        this.field_149384_a = entity.func_145782_y();
        this.field_149383_b = b0;
    }

    @Override
    public void func_148837_a(PacketBuffer packetbuffer) {
        this.field_149384_a = packetbuffer.readInt();
        this.field_149383_b = packetbuffer.readByte();
    }

    @Override
    public void func_148840_b(PacketBuffer packetbuffer) {
        packetbuffer.writeInt(this.field_149384_a);
        packetbuffer.writeByte(this.field_149383_b);
    }

    public void func_148833_a(INetHandlerPlayClient inethandlerplayclient) {
        inethandlerplayclient.func_147267_a(this);
    }

    @Override
    public String func_148835_b() {
        return String.format("id=%d, rot=%d", this.field_149384_a, this.field_149383_b);
    }
}
