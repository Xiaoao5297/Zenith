package net.minecraft.network.play.server;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S33PacketUpdateSign extends Packet {

    private int field_149352_a;
    private int field_149350_b;
    private int field_149351_c;
    private String[] field_149349_d;

    public S33PacketUpdateSign() {}

    public S33PacketUpdateSign(int i, int j, int k, String[] astring) {
        this.field_149352_a = i;
        this.field_149350_b = j;
        this.field_149351_c = k;
        this.field_149349_d = new String[]{astring[0], astring[1], astring[2], astring[3]};
    }

    @Override
    public void func_148837_a(PacketBuffer packetbuffer) {
        this.field_149352_a = packetbuffer.readInt();
        this.field_149350_b = packetbuffer.readShort();
        this.field_149351_c = packetbuffer.readInt();
        this.field_149349_d = new String[4];

        for (int i = 0; i < 4; ++i) {
            this.field_149349_d[i] = packetbuffer.func_150789_c(15);
        }

    }

    @Override
    public void func_148840_b(PacketBuffer packetbuffer) {
        packetbuffer.writeInt(this.field_149352_a);
        packetbuffer.writeShort(this.field_149350_b);
        packetbuffer.writeInt(this.field_149351_c);

        for (int i = 0; i < 4; ++i) {
            packetbuffer.func_150785_a(this.field_149349_d[i]);
        }

    }

    public void func_148833_a(INetHandlerPlayClient inethandlerplayclient) {
        inethandlerplayclient.func_147248_a(this);
    }
}
