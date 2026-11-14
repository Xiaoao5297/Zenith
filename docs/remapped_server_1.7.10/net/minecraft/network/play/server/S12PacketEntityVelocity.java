package net.minecraft.network.play.server;

import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S12PacketEntityVelocity extends Packet {

    private int field_149417_a;
    private int field_149415_b;
    private int field_149416_c;
    private int field_149414_d;

    public S12PacketEntityVelocity() {}

    public S12PacketEntityVelocity(Entity entity) {
        this(entity.func_145782_y(), entity.field_70159_w, entity.field_70181_x, entity.field_70179_y);
    }

    public S12PacketEntityVelocity(int i, double d0, double d1, double d2) {
        this.field_149417_a = i;
        double d3 = 3.9D;

        if (d0 < -d3) {
            d0 = -d3;
        }

        if (d1 < -d3) {
            d1 = -d3;
        }

        if (d2 < -d3) {
            d2 = -d3;
        }

        if (d0 > d3) {
            d0 = d3;
        }

        if (d1 > d3) {
            d1 = d3;
        }

        if (d2 > d3) {
            d2 = d3;
        }

        this.field_149415_b = (int) (d0 * 8000.0D);
        this.field_149416_c = (int) (d1 * 8000.0D);
        this.field_149414_d = (int) (d2 * 8000.0D);
    }

    @Override
    public void func_148837_a(PacketBuffer packetbuffer) {
        this.field_149417_a = packetbuffer.readInt();
        this.field_149415_b = packetbuffer.readShort();
        this.field_149416_c = packetbuffer.readShort();
        this.field_149414_d = packetbuffer.readShort();
    }

    @Override
    public void func_148840_b(PacketBuffer packetbuffer) {
        packetbuffer.writeInt(this.field_149417_a);
        packetbuffer.writeShort(this.field_149415_b);
        packetbuffer.writeShort(this.field_149416_c);
        packetbuffer.writeShort(this.field_149414_d);
    }

    public void func_148833_a(INetHandlerPlayClient inethandlerplayclient) {
        inethandlerplayclient.func_147244_a(this);
    }

    @Override
    public String func_148835_b() {
        return String.format("id=%d, x=%.2f, y=%.2f, z=%.2f", this.field_149417_a, (float) this.field_149415_b / 8000.0F, (float) this.field_149416_c / 8000.0F, (float) this.field_149414_d / 8000.0F);
    }
}
