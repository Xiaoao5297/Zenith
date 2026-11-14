package net.minecraft.network.play.server;

import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S2FPacketSetSlot extends Packet {

    private int field_149179_a;
    private int field_149177_b;
    private ItemStack field_149178_c;

    public S2FPacketSetSlot() {}

    public S2FPacketSetSlot(int i, int j, ItemStack itemstack) {
        this.field_149179_a = i;
        this.field_149177_b = j;
        this.field_149178_c = itemstack == null ? null : itemstack.func_77946_l();
    }

    public void func_148833_a(INetHandlerPlayClient inethandlerplayclient) {
        inethandlerplayclient.func_147266_a(this);
    }

    @Override
    public void func_148837_a(PacketBuffer packetbuffer) {
        this.field_149179_a = packetbuffer.readByte();
        this.field_149177_b = packetbuffer.readShort();
        this.field_149178_c = packetbuffer.func_150791_c();
    }

    @Override
    public void func_148840_b(PacketBuffer packetbuffer) {
        packetbuffer.writeByte(this.field_149179_a);
        packetbuffer.writeShort(this.field_149177_b);
        packetbuffer.func_150788_a(this.field_149178_c);
    }
}
