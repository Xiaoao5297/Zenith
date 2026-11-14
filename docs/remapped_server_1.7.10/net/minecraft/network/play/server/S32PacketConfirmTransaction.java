package net.minecraft.network.play.server;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S32PacketConfirmTransaction extends Packet {

    private int field_148894_a;
    private short field_148892_b;
    private boolean field_148893_c;

    public S32PacketConfirmTransaction() {}

    public S32PacketConfirmTransaction(int i, short short0, boolean flag) {
        this.field_148894_a = i;
        this.field_148892_b = short0;
        this.field_148893_c = flag;
    }

    public void func_148833_a(INetHandlerPlayClient inethandlerplayclient) {
        inethandlerplayclient.func_147239_a(this);
    }

    @Override
    public void func_148837_a(PacketBuffer packetbuffer) {
        this.field_148894_a = packetbuffer.readUnsignedByte();
        this.field_148892_b = packetbuffer.readShort();
        this.field_148893_c = packetbuffer.readBoolean();
    }

    @Override
    public void func_148840_b(PacketBuffer packetbuffer) {
        packetbuffer.writeByte(this.field_148894_a);
        packetbuffer.writeShort(this.field_148892_b);
        packetbuffer.writeBoolean(this.field_148893_c);
    }

    @Override
    public String func_148835_b() {
        return String.format("id=%d, uid=%d, accepted=%b", this.field_148894_a, this.field_148892_b, this.field_148893_c);
    }
}
