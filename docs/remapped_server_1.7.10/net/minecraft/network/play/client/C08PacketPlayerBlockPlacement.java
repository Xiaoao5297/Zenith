package net.minecraft.network.play.client;

import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;

public class C08PacketPlayerBlockPlacement extends Packet {

    private int field_149583_a;
    private int field_149581_b;
    private int field_149582_c;
    private int field_149579_d;
    private ItemStack field_149580_e;
    private float field_149577_f;
    private float field_149578_g;
    private float field_149584_h;

    public C08PacketPlayerBlockPlacement() {}

    @Override
    public void func_148837_a(PacketBuffer packetbuffer) {
        this.field_149583_a = packetbuffer.readInt();
        this.field_149581_b = packetbuffer.readUnsignedByte();
        this.field_149582_c = packetbuffer.readInt();
        this.field_149579_d = packetbuffer.readUnsignedByte();
        this.field_149580_e = packetbuffer.func_150791_c();
        this.field_149577_f = (float) packetbuffer.readUnsignedByte() / 16.0F;
        this.field_149578_g = (float) packetbuffer.readUnsignedByte() / 16.0F;
        this.field_149584_h = (float) packetbuffer.readUnsignedByte() / 16.0F;
    }

    @Override
    public void func_148840_b(PacketBuffer packetbuffer) {
        packetbuffer.writeInt(this.field_149583_a);
        packetbuffer.writeByte(this.field_149581_b);
        packetbuffer.writeInt(this.field_149582_c);
        packetbuffer.writeByte(this.field_149579_d);
        packetbuffer.func_150788_a(this.field_149580_e);
        packetbuffer.writeByte((int) (this.field_149577_f * 16.0F));
        packetbuffer.writeByte((int) (this.field_149578_g * 16.0F));
        packetbuffer.writeByte((int) (this.field_149584_h * 16.0F));
    }

    public void func_148833_a(INetHandlerPlayServer inethandlerplayserver) {
        inethandlerplayserver.func_147346_a(this);
    }

    public int func_149576_c() {
        return this.field_149583_a;
    }

    public int func_149571_d() {
        return this.field_149581_b;
    }

    public int func_149570_e() {
        return this.field_149582_c;
    }

    public int func_149568_f() {
        return this.field_149579_d;
    }

    public ItemStack func_149574_g() {
        return this.field_149580_e;
    }

    public float func_149573_h() {
        return this.field_149577_f;
    }

    public float func_149569_i() {
        return this.field_149578_g;
    }

    public float func_149575_j() {
        return this.field_149584_h;
    }
}
