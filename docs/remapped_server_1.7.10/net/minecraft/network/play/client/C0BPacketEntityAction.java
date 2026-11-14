package net.minecraft.network.play.client;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;

public class C0BPacketEntityAction extends Packet {

    private int field_149517_a;
    private int field_149515_b;
    private int field_149516_c;

    public C0BPacketEntityAction() {}

    @Override
    public void func_148837_a(PacketBuffer packetbuffer) {
        this.field_149517_a = packetbuffer.readInt();
        this.field_149515_b = packetbuffer.readByte();
        this.field_149516_c = packetbuffer.readInt();
    }

    @Override
    public void func_148840_b(PacketBuffer packetbuffer) {
        packetbuffer.writeInt(this.field_149517_a);
        packetbuffer.writeByte(this.field_149515_b);
        packetbuffer.writeInt(this.field_149516_c);
    }

    public void func_148833_a(INetHandlerPlayServer inethandlerplayserver) {
        inethandlerplayserver.func_147357_a(this);
    }

    public int func_149513_d() {
        return this.field_149515_b;
    }

    public int func_149512_e() {
        return this.field_149516_c;
    }
}
