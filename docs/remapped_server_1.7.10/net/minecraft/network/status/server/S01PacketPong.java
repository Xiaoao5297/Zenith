package net.minecraft.network.status.server;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.status.INetHandlerStatusClient;

public class S01PacketPong extends Packet {

    private long field_149293_a;

    public S01PacketPong() {}

    public S01PacketPong(long i) {
        this.field_149293_a = i;
    }

    @Override
    public void func_148837_a(PacketBuffer packetbuffer) {
        this.field_149293_a = packetbuffer.readLong();
    }

    @Override
    public void func_148840_b(PacketBuffer packetbuffer) {
        packetbuffer.writeLong(this.field_149293_a);
    }

    public void func_148833_a(INetHandlerStatusClient inethandlerstatusclient) {
        inethandlerstatusclient.func_147398_a(this);
    }

    @Override
    public boolean func_148836_a() {
        return true;
    }
}
