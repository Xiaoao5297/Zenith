package net.minecraft.network.play.server;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S38PacketPlayerListItem extends Packet {

    private String field_149126_a;
    private boolean field_149124_b;
    private int field_149125_c;

    public S38PacketPlayerListItem() {}

    public S38PacketPlayerListItem(String s, boolean flag, int i) {
        this.field_149126_a = s;
        this.field_149124_b = flag;
        this.field_149125_c = i;
    }

    @Override
    public void func_148837_a(PacketBuffer packetbuffer) {
        this.field_149126_a = packetbuffer.func_150789_c(16);
        this.field_149124_b = packetbuffer.readBoolean();
        this.field_149125_c = packetbuffer.readShort();
    }

    @Override
    public void func_148840_b(PacketBuffer packetbuffer) {
        packetbuffer.func_150785_a(this.field_149126_a);
        packetbuffer.writeBoolean(this.field_149124_b);
        packetbuffer.writeShort(this.field_149125_c);
    }

    public void func_148833_a(INetHandlerPlayClient inethandlerplayclient) {
        inethandlerplayclient.func_147256_a(this);
    }
}
