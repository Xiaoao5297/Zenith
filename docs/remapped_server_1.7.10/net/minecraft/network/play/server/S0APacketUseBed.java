package net.minecraft.network.play.server;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S0APacketUseBed extends Packet {

    private int field_149097_a;
    private int field_149095_b;
    private int field_149096_c;
    private int field_149094_d;

    public S0APacketUseBed() {}

    public S0APacketUseBed(EntityPlayer entityplayer, int i, int j, int k) {
        this.field_149095_b = i;
        this.field_149096_c = j;
        this.field_149094_d = k;
        this.field_149097_a = entityplayer.func_145782_y();
    }

    @Override
    public void func_148837_a(PacketBuffer packetbuffer) {
        this.field_149097_a = packetbuffer.readInt();
        this.field_149095_b = packetbuffer.readInt();
        this.field_149096_c = packetbuffer.readByte();
        this.field_149094_d = packetbuffer.readInt();
    }

    @Override
    public void func_148840_b(PacketBuffer packetbuffer) {
        packetbuffer.writeInt(this.field_149097_a);
        packetbuffer.writeInt(this.field_149095_b);
        packetbuffer.writeByte(this.field_149096_c);
        packetbuffer.writeInt(this.field_149094_d);
    }

    public void func_148833_a(INetHandlerPlayClient inethandlerplayclient) {
        inethandlerplayclient.func_147278_a(this);
    }
}
