package net.minecraft.network.play.server;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S1FPacketSetExperience extends Packet {

    private float field_149401_a;
    private int field_149399_b;
    private int field_149400_c;

    public S1FPacketSetExperience() {}

    public S1FPacketSetExperience(float f, int i, int j) {
        this.field_149401_a = f;
        this.field_149399_b = i;
        this.field_149400_c = j;
    }

    @Override
    public void func_148837_a(PacketBuffer packetbuffer) {
        this.field_149401_a = packetbuffer.readFloat();
        this.field_149400_c = packetbuffer.readShort();
        this.field_149399_b = packetbuffer.readShort();
    }

    @Override
    public void func_148840_b(PacketBuffer packetbuffer) {
        packetbuffer.writeFloat(this.field_149401_a);
        packetbuffer.writeShort(this.field_149400_c);
        packetbuffer.writeShort(this.field_149399_b);
    }

    public void func_148833_a(INetHandlerPlayClient inethandlerplayclient) {
        inethandlerplayclient.func_147295_a(this);
    }
}
