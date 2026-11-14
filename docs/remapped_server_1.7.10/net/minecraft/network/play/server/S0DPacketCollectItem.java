package net.minecraft.network.play.server;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S0DPacketCollectItem extends Packet {

    private int field_149357_a;
    private int field_149356_b;

    public S0DPacketCollectItem() {}

    public S0DPacketCollectItem(int i, int j) {
        this.field_149357_a = i;
        this.field_149356_b = j;
    }

    @Override
    public void func_148837_a(PacketBuffer packetbuffer) {
        this.field_149357_a = packetbuffer.readInt();
        this.field_149356_b = packetbuffer.readInt();
    }

    @Override
    public void func_148840_b(PacketBuffer packetbuffer) {
        packetbuffer.writeInt(this.field_149357_a);
        packetbuffer.writeInt(this.field_149356_b);
    }

    public void func_148833_a(INetHandlerPlayClient inethandlerplayclient) {
        inethandlerplayclient.func_147246_a(this);
    }
}
