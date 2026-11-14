package net.minecraft.network.play.server;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S2APacketParticles extends Packet {

    private String field_149236_a;
    private float field_149234_b;
    private float field_149235_c;
    private float field_149232_d;
    private float field_149233_e;
    private float field_149230_f;
    private float field_149231_g;
    private float field_149237_h;
    private int field_149238_i;

    public S2APacketParticles() {}

    public S2APacketParticles(String s, float f, float f1, float f2, float f3, float f4, float f5, float f6, int i) {
        this.field_149236_a = s;
        this.field_149234_b = f;
        this.field_149235_c = f1;
        this.field_149232_d = f2;
        this.field_149233_e = f3;
        this.field_149230_f = f4;
        this.field_149231_g = f5;
        this.field_149237_h = f6;
        this.field_149238_i = i;
    }

    @Override
    public void func_148837_a(PacketBuffer packetbuffer) {
        this.field_149236_a = packetbuffer.func_150789_c(64);
        this.field_149234_b = packetbuffer.readFloat();
        this.field_149235_c = packetbuffer.readFloat();
        this.field_149232_d = packetbuffer.readFloat();
        this.field_149233_e = packetbuffer.readFloat();
        this.field_149230_f = packetbuffer.readFloat();
        this.field_149231_g = packetbuffer.readFloat();
        this.field_149237_h = packetbuffer.readFloat();
        this.field_149238_i = packetbuffer.readInt();
    }

    @Override
    public void func_148840_b(PacketBuffer packetbuffer) {
        packetbuffer.func_150785_a(this.field_149236_a);
        packetbuffer.writeFloat(this.field_149234_b);
        packetbuffer.writeFloat(this.field_149235_c);
        packetbuffer.writeFloat(this.field_149232_d);
        packetbuffer.writeFloat(this.field_149233_e);
        packetbuffer.writeFloat(this.field_149230_f);
        packetbuffer.writeFloat(this.field_149231_g);
        packetbuffer.writeFloat(this.field_149237_h);
        packetbuffer.writeInt(this.field_149238_i);
    }

    public void func_148833_a(INetHandlerPlayClient inethandlerplayclient) {
        inethandlerplayclient.func_147289_a(this);
    }
}
