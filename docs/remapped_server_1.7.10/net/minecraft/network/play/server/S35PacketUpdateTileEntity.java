package net.minecraft.network.play.server;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S35PacketUpdateTileEntity extends Packet {

    private int field_148863_a;
    private int field_148861_b;
    private int field_148862_c;
    private int field_148859_d;
    private NBTTagCompound field_148860_e;

    public S35PacketUpdateTileEntity() {}

    public S35PacketUpdateTileEntity(int i, int j, int k, int l, NBTTagCompound nbttagcompound) {
        this.field_148863_a = i;
        this.field_148861_b = j;
        this.field_148862_c = k;
        this.field_148859_d = l;
        this.field_148860_e = nbttagcompound;
    }

    @Override
    public void func_148837_a(PacketBuffer packetbuffer) {
        this.field_148863_a = packetbuffer.readInt();
        this.field_148861_b = packetbuffer.readShort();
        this.field_148862_c = packetbuffer.readInt();
        this.field_148859_d = packetbuffer.readUnsignedByte();
        this.field_148860_e = packetbuffer.func_150793_b();
    }

    @Override
    public void func_148840_b(PacketBuffer packetbuffer) {
        packetbuffer.writeInt(this.field_148863_a);
        packetbuffer.writeShort(this.field_148861_b);
        packetbuffer.writeInt(this.field_148862_c);
        packetbuffer.writeByte((byte) this.field_148859_d);
        packetbuffer.func_150786_a(this.field_148860_e);
    }

    public void func_148833_a(INetHandlerPlayClient inethandlerplayclient) {
        inethandlerplayclient.func_147273_a(this);
    }
}
