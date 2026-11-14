package net.minecraft.network.play.server;

import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S04PacketEntityEquipment extends Packet {

    private int field_149394_a;
    private int field_149392_b;
    private ItemStack field_149393_c;

    public S04PacketEntityEquipment() {}

    public S04PacketEntityEquipment(int i, int j, ItemStack itemstack) {
        this.field_149394_a = i;
        this.field_149392_b = j;
        this.field_149393_c = itemstack == null ? null : itemstack.func_77946_l();
    }

    @Override
    public void func_148837_a(PacketBuffer packetbuffer) {
        this.field_149394_a = packetbuffer.readInt();
        this.field_149392_b = packetbuffer.readShort();
        this.field_149393_c = packetbuffer.func_150791_c();
    }

    @Override
    public void func_148840_b(PacketBuffer packetbuffer) {
        packetbuffer.writeInt(this.field_149394_a);
        packetbuffer.writeShort(this.field_149392_b);
        packetbuffer.func_150788_a(this.field_149393_c);
    }

    public void func_148833_a(INetHandlerPlayClient inethandlerplayclient) {
        inethandlerplayclient.func_147242_a(this);
    }

    @Override
    public String func_148835_b() {
        return String.format("entity=%d, slot=%d, item=%s", this.field_149394_a, this.field_149392_b, this.field_149393_c);
    }
}
