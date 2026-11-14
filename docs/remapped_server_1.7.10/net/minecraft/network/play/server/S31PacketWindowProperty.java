package net.minecraft.network.play.server;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S31PacketWindowProperty extends Packet {

    private int field_149186_a;
    private int field_149184_b;
    private int field_149185_c;

    public S31PacketWindowProperty() {}

    public S31PacketWindowProperty(int i, int j, int k) {
        this.field_149186_a = i;
        this.field_149184_b = j;
        this.field_149185_c = k;
    }

    public void func_148833_a(INetHandlerPlayClient inethandlerplayclient) {
        inethandlerplayclient.func_147245_a(this);
    }

    @Override
    public void func_148837_a(PacketBuffer packetbuffer) {
        this.field_149186_a = packetbuffer.readUnsignedByte();
        this.field_149184_b = packetbuffer.readShort();
        this.field_149185_c = packetbuffer.readShort();
    }

    @Override
    public void func_148840_b(PacketBuffer packetbuffer) {
        packetbuffer.writeByte(this.field_149186_a);
        packetbuffer.writeShort(this.field_149184_b);
        packetbuffer.writeShort(this.field_149185_c);
    }
}
