package net.minecraft.network.play.server;

import net.minecraft.block.Block;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S24PacketBlockAction extends Packet {

    private int field_148876_a;
    private int field_148874_b;
    private int field_148875_c;
    private int field_148872_d;
    private int field_148873_e;
    private Block field_148871_f;

    public S24PacketBlockAction() {}

    public S24PacketBlockAction(int i, int j, int k, Block block, int l, int i1) {
        this.field_148876_a = i;
        this.field_148874_b = j;
        this.field_148875_c = k;
        this.field_148872_d = l;
        this.field_148873_e = i1;
        this.field_148871_f = block;
    }

    @Override
    public void func_148837_a(PacketBuffer packetbuffer) {
        this.field_148876_a = packetbuffer.readInt();
        this.field_148874_b = packetbuffer.readShort();
        this.field_148875_c = packetbuffer.readInt();
        this.field_148872_d = packetbuffer.readUnsignedByte();
        this.field_148873_e = packetbuffer.readUnsignedByte();
        this.field_148871_f = Block.func_149729_e(packetbuffer.func_150792_a() & 4095);
    }

    @Override
    public void func_148840_b(PacketBuffer packetbuffer) {
        packetbuffer.writeInt(this.field_148876_a);
        packetbuffer.writeShort(this.field_148874_b);
        packetbuffer.writeInt(this.field_148875_c);
        packetbuffer.writeByte(this.field_148872_d);
        packetbuffer.writeByte(this.field_148873_e);
        packetbuffer.func_150787_b(Block.func_149682_b(this.field_148871_f) & 4095);
    }

    public void func_148833_a(INetHandlerPlayClient inethandlerplayclient) {
        inethandlerplayclient.func_147261_a(this);
    }
}
