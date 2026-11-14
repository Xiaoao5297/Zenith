package net.minecraft.network.status.client;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.status.INetHandlerStatusServer;

public class C01PacketPing extends Packet {

    private long field_149290_a;

    public C01PacketPing() {}

    @Override
    public void func_148837_a(PacketBuffer packetbuffer) {
        this.field_149290_a = packetbuffer.readLong();
    }

    @Override
    public void func_148840_b(PacketBuffer packetbuffer) {
        packetbuffer.writeLong(this.field_149290_a);
    }

    public void func_148833_a(INetHandlerStatusServer inethandlerstatusserver) {
        inethandlerstatusserver.func_147311_a(this);
    }

    @Override
    public boolean func_148836_a() {
        return true;
    }

    public long func_149289_c() {
        return this.field_149290_a;
    }
}
