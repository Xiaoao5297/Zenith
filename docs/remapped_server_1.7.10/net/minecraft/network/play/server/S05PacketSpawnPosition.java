package net.minecraft.network.play.server;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S05PacketSpawnPosition extends Packet {

    private int field_149364_a;
    private int field_149362_b;
    private int field_149363_c;

    public S05PacketSpawnPosition() {}

    public S05PacketSpawnPosition(int i, int j, int k) {
        this.field_149364_a = i;
        this.field_149362_b = j;
        this.field_149363_c = k;
    }

    @Override
    public void func_148837_a(PacketBuffer packetbuffer) {
        this.field_149364_a = packetbuffer.readInt();
        this.field_149362_b = packetbuffer.readInt();
        this.field_149363_c = packetbuffer.readInt();
    }

    @Override
    public void func_148840_b(PacketBuffer packetbuffer) {
        packetbuffer.writeInt(this.field_149364_a);
        packetbuffer.writeInt(this.field_149362_b);
        packetbuffer.writeInt(this.field_149363_c);
    }

    public void func_148833_a(INetHandlerPlayClient inethandlerplayclient) {
        inethandlerplayclient.func_147271_a(this);
    }

    @Override
    public boolean func_148836_a() {
        return false;
    }

    @Override
    public String func_148835_b() {
        return String.format("x=%d, y=%d, z=%d", this.field_149364_a, this.field_149362_b, this.field_149363_c);
    }
}
