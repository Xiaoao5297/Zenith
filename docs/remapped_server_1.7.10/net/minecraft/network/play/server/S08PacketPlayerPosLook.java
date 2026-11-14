package net.minecraft.network.play.server;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S08PacketPlayerPosLook extends Packet {

    private double field_148940_a;
    private double field_148938_b;
    private double field_148939_c;
    private float field_148936_d;
    private float field_148937_e;
    private boolean field_148935_f;

    public S08PacketPlayerPosLook() {}

    public S08PacketPlayerPosLook(double d0, double d1, double d2, float f, float f1, boolean flag) {
        this.field_148940_a = d0;
        this.field_148938_b = d1;
        this.field_148939_c = d2;
        this.field_148936_d = f;
        this.field_148937_e = f1;
        this.field_148935_f = flag;
    }

    @Override
    public void func_148837_a(PacketBuffer packetbuffer) {
        this.field_148940_a = packetbuffer.readDouble();
        this.field_148938_b = packetbuffer.readDouble();
        this.field_148939_c = packetbuffer.readDouble();
        this.field_148936_d = packetbuffer.readFloat();
        this.field_148937_e = packetbuffer.readFloat();
        this.field_148935_f = packetbuffer.readBoolean();
    }

    @Override
    public void func_148840_b(PacketBuffer packetbuffer) {
        packetbuffer.writeDouble(this.field_148940_a);
        packetbuffer.writeDouble(this.field_148938_b);
        packetbuffer.writeDouble(this.field_148939_c);
        packetbuffer.writeFloat(this.field_148936_d);
        packetbuffer.writeFloat(this.field_148937_e);
        packetbuffer.writeBoolean(this.field_148935_f);
    }

    public void func_148833_a(INetHandlerPlayClient inethandlerplayclient) {
        inethandlerplayclient.func_147258_a(this);
    }
}
