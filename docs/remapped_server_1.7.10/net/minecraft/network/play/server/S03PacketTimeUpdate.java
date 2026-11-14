package net.minecraft.network.play.server;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S03PacketTimeUpdate extends Packet {

    private long field_149369_a;
    private long field_149368_b;

    public S03PacketTimeUpdate() {}

    public S03PacketTimeUpdate(long i, long j, boolean flag) {
        this.field_149369_a = i;
        this.field_149368_b = j;
        if (!flag) {
            this.field_149368_b = -this.field_149368_b;
            if (this.field_149368_b == 0L) {
                this.field_149368_b = -1L;
            }
        }

    }

    @Override
    public void func_148837_a(PacketBuffer packetbuffer) {
        this.field_149369_a = packetbuffer.readLong();
        this.field_149368_b = packetbuffer.readLong();
    }

    @Override
    public void func_148840_b(PacketBuffer packetbuffer) {
        packetbuffer.writeLong(this.field_149369_a);
        packetbuffer.writeLong(this.field_149368_b);
    }

    public void func_148833_a(INetHandlerPlayClient inethandlerplayclient) {
        inethandlerplayclient.func_147285_a(this);
    }

    @Override
    public String func_148835_b() {
        return String.format("time=%d,dtime=%d", this.field_149369_a, this.field_149368_b);
    }
}
