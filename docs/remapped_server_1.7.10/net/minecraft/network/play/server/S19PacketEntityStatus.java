package net.minecraft.network.play.server;

import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S19PacketEntityStatus extends Packet {

    private int field_149164_a;
    private byte field_149163_b;

    public S19PacketEntityStatus() {}

    public S19PacketEntityStatus(Entity entity, byte b0) {
        this.field_149164_a = entity.func_145782_y();
        this.field_149163_b = b0;
    }

    @Override
    public void func_148837_a(PacketBuffer packetbuffer) {
        this.field_149164_a = packetbuffer.readInt();
        this.field_149163_b = packetbuffer.readByte();
    }

    @Override
    public void func_148840_b(PacketBuffer packetbuffer) {
        packetbuffer.writeInt(this.field_149164_a);
        packetbuffer.writeByte(this.field_149163_b);
    }

    public void func_148833_a(INetHandlerPlayClient inethandlerplayclient) {
        inethandlerplayclient.func_147236_a(this);
    }
}
