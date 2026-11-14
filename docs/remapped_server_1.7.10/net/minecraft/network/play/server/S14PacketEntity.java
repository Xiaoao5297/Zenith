package net.minecraft.network.play.server;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S14PacketEntity extends Packet {

    protected int field_149074_a;
    protected byte field_149072_b;
    protected byte field_149073_c;
    protected byte field_149070_d;
    protected byte field_149071_e;
    protected byte field_149068_f;
    protected boolean field_149069_g;

    public S14PacketEntity() {}

    public S14PacketEntity(int i) {
        this.field_149074_a = i;
    }

    @Override
    public void func_148837_a(PacketBuffer packetbuffer) {
        this.field_149074_a = packetbuffer.readInt();
    }

    @Override
    public void func_148840_b(PacketBuffer packetbuffer) {
        packetbuffer.writeInt(this.field_149074_a);
    }

    public void func_148833_a(INetHandlerPlayClient inethandlerplayclient) {
        inethandlerplayclient.func_147259_a(this);
    }

    @Override
    public String func_148835_b() {
        return String.format("id=%d", this.field_149074_a);
    }

    @Override
    public String toString() {
        return "Entity_" + super.toString();
    }
}
