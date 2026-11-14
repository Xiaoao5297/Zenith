package net.minecraft.network.play.server;

import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S1BPacketEntityAttach extends Packet {

    private int field_149408_a;
    private int field_149406_b;
    private int field_149407_c;

    public S1BPacketEntityAttach() {}

    public S1BPacketEntityAttach(int i, Entity entity, Entity entity1) {
        this.field_149408_a = i;
        this.field_149406_b = entity.func_145782_y();
        this.field_149407_c = entity1 != null ? entity1.func_145782_y() : -1;
    }

    @Override
    public void func_148837_a(PacketBuffer packetbuffer) {
        this.field_149406_b = packetbuffer.readInt();
        this.field_149407_c = packetbuffer.readInt();
        this.field_149408_a = packetbuffer.readUnsignedByte();
    }

    @Override
    public void func_148840_b(PacketBuffer packetbuffer) {
        packetbuffer.writeInt(this.field_149406_b);
        packetbuffer.writeInt(this.field_149407_c);
        packetbuffer.writeByte(this.field_149408_a);
    }

    public void func_148833_a(INetHandlerPlayClient inethandlerplayclient) {
        inethandlerplayclient.func_147243_a(this);
    }
}
