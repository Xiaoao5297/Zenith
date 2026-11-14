package net.minecraft.network.play.client;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;

public class C0FPacketConfirmTransaction extends Packet {

    private int field_149536_a;
    private short field_149534_b;
    private boolean field_149535_c;

    public C0FPacketConfirmTransaction() {}

    public void func_148833_a(INetHandlerPlayServer inethandlerplayserver) {
        inethandlerplayserver.func_147339_a(this);
    }

    @Override
    public void func_148837_a(PacketBuffer packetbuffer) {
        this.field_149536_a = packetbuffer.readByte();
        this.field_149534_b = packetbuffer.readShort();
        this.field_149535_c = packetbuffer.readByte() != 0;
    }

    @Override
    public void func_148840_b(PacketBuffer packetbuffer) {
        packetbuffer.writeByte(this.field_149536_a);
        packetbuffer.writeShort(this.field_149534_b);
        packetbuffer.writeByte(this.field_149535_c ? 1 : 0);
    }

    @Override
    public String func_148835_b() {
        return String.format("id=%d, uid=%d, accepted=%b", this.field_149536_a, this.field_149534_b, this.field_149535_c);
    }

    public int func_149532_c() {
        return this.field_149536_a;
    }

    public short func_149533_d() {
        return this.field_149534_b;
    }
}
