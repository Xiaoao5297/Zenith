package net.minecraft.network.play.server;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S28PacketEffect extends Packet {

    private int field_149251_a;
    private int field_149249_b;
    private int field_149250_c;
    private int field_149247_d;
    private int field_149248_e;
    private boolean field_149246_f;

    public S28PacketEffect() {}

    public S28PacketEffect(int i, int j, int k, int l, int i1, boolean flag) {
        this.field_149251_a = i;
        this.field_149250_c = j;
        this.field_149247_d = k;
        this.field_149248_e = l;
        this.field_149249_b = i1;
        this.field_149246_f = flag;
    }

    @Override
    public void func_148837_a(PacketBuffer packetbuffer) {
        this.field_149251_a = packetbuffer.readInt();
        this.field_149250_c = packetbuffer.readInt();
        this.field_149247_d = packetbuffer.readByte() & 255;
        this.field_149248_e = packetbuffer.readInt();
        this.field_149249_b = packetbuffer.readInt();
        this.field_149246_f = packetbuffer.readBoolean();
    }

    @Override
    public void func_148840_b(PacketBuffer packetbuffer) {
        packetbuffer.writeInt(this.field_149251_a);
        packetbuffer.writeInt(this.field_149250_c);
        packetbuffer.writeByte(this.field_149247_d & 255);
        packetbuffer.writeInt(this.field_149248_e);
        packetbuffer.writeInt(this.field_149249_b);
        packetbuffer.writeBoolean(this.field_149246_f);
    }

    public void func_148833_a(INetHandlerPlayClient inethandlerplayclient) {
        inethandlerplayclient.func_147277_a(this);
    }
}
