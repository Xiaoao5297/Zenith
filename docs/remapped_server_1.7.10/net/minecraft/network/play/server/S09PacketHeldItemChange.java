package net.minecraft.network.play.server;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S09PacketHeldItemChange extends Packet {

    private int field_149387_a;

    public S09PacketHeldItemChange() {}

    public S09PacketHeldItemChange(int i) {
        this.field_149387_a = i;
    }

    @Override
    public void func_148837_a(PacketBuffer packetbuffer) {
        this.field_149387_a = packetbuffer.readByte();
    }

    @Override
    public void func_148840_b(PacketBuffer packetbuffer) {
        packetbuffer.writeByte(this.field_149387_a);
    }

    public void func_148833_a(INetHandlerPlayClient inethandlerplayclient) {
        inethandlerplayclient.func_147257_a(this);
    }
}
