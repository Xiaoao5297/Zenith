package net.minecraft.network.status.client;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.status.INetHandlerStatusServer;

public class C00PacketServerQuery extends Packet {

    public C00PacketServerQuery() {}

    @Override
    public void func_148837_a(PacketBuffer packetbuffer) {}

    @Override
    public void func_148840_b(PacketBuffer packetbuffer) {}

    public void func_148833_a(INetHandlerStatusServer inethandlerstatusserver) {
        inethandlerstatusserver.func_147312_a(this);
    }

    @Override
    public boolean func_148836_a() {
        return true;
    }
}
