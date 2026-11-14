package net.minecraft.network.play.server;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import org.apache.commons.lang3.Validate;

public class S29PacketSoundEffect extends Packet {

    private String field_149219_a;
    private int field_149217_b;
    private int field_149218_c = Integer.MAX_VALUE;
    private int field_149215_d;
    private float field_149216_e;
    private int field_149214_f;

    public S29PacketSoundEffect() {}

    public S29PacketSoundEffect(String s, double d0, double d1, double d2, float f, float f1) {
        Validate.notNull(s, "name");
        this.field_149219_a = s;
        this.field_149217_b = (int) (d0 * 8.0D);
        this.field_149218_c = (int) (d1 * 8.0D);
        this.field_149215_d = (int) (d2 * 8.0D);
        this.field_149216_e = f;
        this.field_149214_f = (int) (f1 * 63.0F);
        if (this.field_149214_f < 0) {
            this.field_149214_f = 0;
        }

        if (this.field_149214_f > 255) {
            this.field_149214_f = 255;
        }

    }

    @Override
    public void func_148837_a(PacketBuffer packetbuffer) {
        this.field_149219_a = packetbuffer.func_150789_c(256);
        this.field_149217_b = packetbuffer.readInt();
        this.field_149218_c = packetbuffer.readInt();
        this.field_149215_d = packetbuffer.readInt();
        this.field_149216_e = packetbuffer.readFloat();
        this.field_149214_f = packetbuffer.readUnsignedByte();
    }

    @Override
    public void func_148840_b(PacketBuffer packetbuffer) {
        packetbuffer.func_150785_a(this.field_149219_a);
        packetbuffer.writeInt(this.field_149217_b);
        packetbuffer.writeInt(this.field_149218_c);
        packetbuffer.writeInt(this.field_149215_d);
        packetbuffer.writeFloat(this.field_149216_e);
        packetbuffer.writeByte(this.field_149214_f);
    }

    public void func_148833_a(INetHandlerPlayClient inethandlerplayclient) {
        inethandlerplayclient.func_147255_a(this);
    }
}
