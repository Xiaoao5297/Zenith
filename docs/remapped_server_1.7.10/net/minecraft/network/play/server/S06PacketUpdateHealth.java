package net.minecraft.network.play.server;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S06PacketUpdateHealth extends Packet {

    private float field_149336_a;
    private int field_149334_b;
    private float field_149335_c;

    public S06PacketUpdateHealth() {}

    public S06PacketUpdateHealth(float f, int i, float f1) {
        this.field_149336_a = f;
        this.field_149334_b = i;
        this.field_149335_c = f1;
    }

    @Override
    public void func_148837_a(PacketBuffer packetbuffer) {
        this.field_149336_a = packetbuffer.readFloat();
        this.field_149334_b = packetbuffer.readShort();
        this.field_149335_c = packetbuffer.readFloat();
    }

    @Override
    public void func_148840_b(PacketBuffer packetbuffer) {
        packetbuffer.writeFloat(this.field_149336_a);
        packetbuffer.writeShort(this.field_149334_b);
        packetbuffer.writeFloat(this.field_149335_c);
    }

    public void func_148833_a(INetHandlerPlayClient inethandlerplayclient) {
        inethandlerplayclient.func_147249_a(this);
    }
}
