package net.minecraft.network.play.server;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S2DPacketOpenWindow extends Packet {

    private int field_148909_a;
    private int field_148907_b;
    private String field_148908_c;
    private int field_148905_d;
    private boolean field_148906_e;
    private int field_148904_f;

    public S2DPacketOpenWindow() {}

    public S2DPacketOpenWindow(int i, int j, String s, int k, boolean flag) {
        this.field_148909_a = i;
        this.field_148907_b = j;
        this.field_148908_c = s;
        this.field_148905_d = k;
        this.field_148906_e = flag;
    }

    public S2DPacketOpenWindow(int i, int j, String s, int k, boolean flag, int l) {
        this(i, j, s, k, flag);
        this.field_148904_f = l;
    }

    public void func_148833_a(INetHandlerPlayClient inethandlerplayclient) {
        inethandlerplayclient.func_147265_a(this);
    }

    @Override
    public void func_148837_a(PacketBuffer packetbuffer) {
        this.field_148909_a = packetbuffer.readUnsignedByte();
        this.field_148907_b = packetbuffer.readUnsignedByte();
        this.field_148908_c = packetbuffer.func_150789_c(32);
        this.field_148905_d = packetbuffer.readUnsignedByte();
        this.field_148906_e = packetbuffer.readBoolean();
        if (this.field_148907_b == 11) {
            this.field_148904_f = packetbuffer.readInt();
        }

    }

    @Override
    public void func_148840_b(PacketBuffer packetbuffer) {
        packetbuffer.writeByte(this.field_148909_a);
        packetbuffer.writeByte(this.field_148907_b);
        packetbuffer.func_150785_a(this.field_148908_c);
        packetbuffer.writeByte(this.field_148905_d);
        packetbuffer.writeBoolean(this.field_148906_e);
        if (this.field_148907_b == 11) {
            packetbuffer.writeInt(this.field_148904_f);
        }

    }
}
