package net.minecraft.network.play.server;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S25PacketBlockBreakAnim extends Packet {

    private int field_148852_a;
    private int field_148850_b;
    private int field_148851_c;
    private int field_148848_d;
    private int field_148849_e;

    public S25PacketBlockBreakAnim() {}

    public S25PacketBlockBreakAnim(int i, int j, int k, int l, int i1) {
        this.field_148852_a = i;
        this.field_148850_b = j;
        this.field_148851_c = k;
        this.field_148848_d = l;
        this.field_148849_e = i1;
    }

    @Override
    public void func_148837_a(PacketBuffer packetbuffer) {
        this.field_148852_a = packetbuffer.func_150792_a();
        this.field_148850_b = packetbuffer.readInt();
        this.field_148851_c = packetbuffer.readInt();
        this.field_148848_d = packetbuffer.readInt();
        this.field_148849_e = packetbuffer.readUnsignedByte();
    }

    @Override
    public void func_148840_b(PacketBuffer packetbuffer) {
        packetbuffer.func_150787_b(this.field_148852_a);
        packetbuffer.writeInt(this.field_148850_b);
        packetbuffer.writeInt(this.field_148851_c);
        packetbuffer.writeInt(this.field_148848_d);
        packetbuffer.writeByte(this.field_148849_e);
    }

    public void func_148833_a(INetHandlerPlayClient inethandlerplayclient) {
        inethandlerplayclient.func_147294_a(this);
    }
}
