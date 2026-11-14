package net.minecraft.network.play.client;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;

public class C00PacketKeepAlive extends Packet {

    private int field_149461_a;

    public C00PacketKeepAlive() {}

    public void func_148833_a(INetHandlerPlayServer inethandlerplayserver) {
        inethandlerplayserver.func_147353_a(this);
    }

    @Override
    public void func_148837_a(PacketBuffer packetbuffer) {
        this.field_149461_a = packetbuffer.readInt();
    }

    @Override
    public void func_148840_b(PacketBuffer packetbuffer) {
        packetbuffer.writeInt(this.field_149461_a);
    }

    @Override
    public boolean func_148836_a() {
        return true;
    }

    public int func_149460_c() {
        return this.field_149461_a;
    }
}
