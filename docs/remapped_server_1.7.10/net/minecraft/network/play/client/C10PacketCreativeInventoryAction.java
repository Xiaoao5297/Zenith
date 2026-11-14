package net.minecraft.network.play.client;

import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;

public class C10PacketCreativeInventoryAction extends Packet {

    private int field_149629_a;
    private ItemStack field_149628_b;

    public C10PacketCreativeInventoryAction() {}

    public void func_148833_a(INetHandlerPlayServer inethandlerplayserver) {
        inethandlerplayserver.func_147344_a(this);
    }

    @Override
    public void func_148837_a(PacketBuffer packetbuffer) {
        this.field_149629_a = packetbuffer.readShort();
        this.field_149628_b = packetbuffer.func_150791_c();
    }

    @Override
    public void func_148840_b(PacketBuffer packetbuffer) {
        packetbuffer.writeShort(this.field_149629_a);
        packetbuffer.func_150788_a(this.field_149628_b);
    }

    public int func_149627_c() {
        return this.field_149629_a;
    }

    public ItemStack func_149625_d() {
        return this.field_149628_b;
    }
}
