package net.minecraft.network.play.server;

import net.minecraft.block.Block;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.world.World;

public class S23PacketBlockChange extends Packet {

    private int field_148887_a;
    private int field_148885_b;
    private int field_148886_c;
    private Block field_148883_d;
    private int field_148884_e;

    public S23PacketBlockChange() {}

    public S23PacketBlockChange(int i, int j, int k, World world) {
        this.field_148887_a = i;
        this.field_148885_b = j;
        this.field_148886_c = k;
        this.field_148883_d = world.func_147439_a(i, j, k);
        this.field_148884_e = world.func_72805_g(i, j, k);
    }

    @Override
    public void func_148837_a(PacketBuffer packetbuffer) {
        this.field_148887_a = packetbuffer.readInt();
        this.field_148885_b = packetbuffer.readUnsignedByte();
        this.field_148886_c = packetbuffer.readInt();
        this.field_148883_d = Block.func_149729_e(packetbuffer.func_150792_a());
        this.field_148884_e = packetbuffer.readUnsignedByte();
    }

    @Override
    public void func_148840_b(PacketBuffer packetbuffer) {
        packetbuffer.writeInt(this.field_148887_a);
        packetbuffer.writeByte(this.field_148885_b);
        packetbuffer.writeInt(this.field_148886_c);
        packetbuffer.func_150787_b(Block.func_149682_b(this.field_148883_d));
        packetbuffer.writeByte(this.field_148884_e);
    }

    public void func_148833_a(INetHandlerPlayClient inethandlerplayclient) {
        inethandlerplayclient.func_147234_a(this);
    }

    @Override
    public String func_148835_b() {
        return String.format("type=%d, data=%d, x=%d, y=%d, z=%d", Block.func_149682_b(this.field_148883_d), this.field_148884_e, this.field_148887_a, this.field_148885_b, this.field_148886_c);
    }
}
