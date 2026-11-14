package net.minecraft.network.play.server;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S36PacketSignEditorOpen extends Packet {

    private int field_149133_a;
    private int field_149131_b;
    private int field_149132_c;

    public S36PacketSignEditorOpen() {}

    public S36PacketSignEditorOpen(int i, int j, int k) {
        this.field_149133_a = i;
        this.field_149131_b = j;
        this.field_149132_c = k;
    }

    public void func_148833_a(INetHandlerPlayClient inethandlerplayclient) {
        inethandlerplayclient.func_147268_a(this);
    }

    @Override
    public void func_148837_a(PacketBuffer packetbuffer) {
        this.field_149133_a = packetbuffer.readInt();
        this.field_149131_b = packetbuffer.readInt();
        this.field_149132_c = packetbuffer.readInt();
    }

    @Override
    public void func_148840_b(PacketBuffer packetbuffer) {
        packetbuffer.writeInt(this.field_149133_a);
        packetbuffer.writeInt(this.field_149131_b);
        packetbuffer.writeInt(this.field_149132_c);
    }
}
