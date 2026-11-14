package net.minecraft.network.play.server;

import java.util.List;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S30PacketWindowItems extends Packet {

    private int field_148914_a;
    private ItemStack[] field_148913_b;

    public S30PacketWindowItems() {}

    public S30PacketWindowItems(int i, List list) {
        this.field_148914_a = i;
        this.field_148913_b = new ItemStack[list.size()];

        for (int j = 0; j < this.field_148913_b.length; ++j) {
            ItemStack itemstack = (ItemStack) list.get(j);

            this.field_148913_b[j] = itemstack == null ? null : itemstack.func_77946_l();
        }

    }

    @Override
    public void func_148837_a(PacketBuffer packetbuffer) {
        this.field_148914_a = packetbuffer.readUnsignedByte();
        short short0 = packetbuffer.readShort();

        this.field_148913_b = new ItemStack[short0];

        for (int i = 0; i < short0; ++i) {
            this.field_148913_b[i] = packetbuffer.func_150791_c();
        }

    }

    @Override
    public void func_148840_b(PacketBuffer packetbuffer) {
        packetbuffer.writeByte(this.field_148914_a);
        packetbuffer.writeShort(this.field_148913_b.length);
        ItemStack[] aitemstack = this.field_148913_b;
        int i = aitemstack.length;

        for (int j = 0; j < i; ++j) {
            ItemStack itemstack = aitemstack[j];

            packetbuffer.func_150788_a(itemstack);
        }

    }

    public void func_148833_a(INetHandlerPlayClient inethandlerplayclient) {
        inethandlerplayclient.func_147241_a(this);
    }
}
