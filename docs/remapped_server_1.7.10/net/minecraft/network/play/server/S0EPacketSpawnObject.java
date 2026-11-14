package net.minecraft.network.play.server;

import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.MathHelper;

public class S0EPacketSpawnObject extends Packet {

    private int field_149018_a;
    private int field_149016_b;
    private int field_149017_c;
    private int field_149014_d;
    private int field_149015_e;
    private int field_149012_f;
    private int field_149013_g;
    private int field_149021_h;
    private int field_149022_i;
    private int field_149019_j;
    private int field_149020_k;

    public S0EPacketSpawnObject() {}

    public S0EPacketSpawnObject(Entity entity, int i) {
        this(entity, i, 0);
    }

    public S0EPacketSpawnObject(Entity entity, int i, int j) {
        this.field_149018_a = entity.func_145782_y();
        this.field_149016_b = MathHelper.func_76128_c(entity.field_70165_t * 32.0D);
        this.field_149017_c = MathHelper.func_76128_c(entity.field_70163_u * 32.0D);
        this.field_149014_d = MathHelper.func_76128_c(entity.field_70161_v * 32.0D);
        this.field_149021_h = MathHelper.func_76141_d(entity.field_70125_A * 256.0F / 360.0F);
        this.field_149022_i = MathHelper.func_76141_d(entity.field_70177_z * 256.0F / 360.0F);
        this.field_149019_j = i;
        this.field_149020_k = j;
        if (j > 0) {
            double d0 = entity.field_70159_w;
            double d1 = entity.field_70181_x;
            double d2 = entity.field_70179_y;
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

            this.field_149015_e = (int) (d0 * 8000.0D);
            this.field_149012_f = (int) (d1 * 8000.0D);
            this.field_149013_g = (int) (d2 * 8000.0D);
        }

    }

    @Override
    public void func_148837_a(PacketBuffer packetbuffer) {
        this.field_149018_a = packetbuffer.func_150792_a();
        this.field_149019_j = packetbuffer.readByte();
        this.field_149016_b = packetbuffer.readInt();
        this.field_149017_c = packetbuffer.readInt();
        this.field_149014_d = packetbuffer.readInt();
        this.field_149021_h = packetbuffer.readByte();
        this.field_149022_i = packetbuffer.readByte();
        this.field_149020_k = packetbuffer.readInt();
        if (this.field_149020_k > 0) {
            this.field_149015_e = packetbuffer.readShort();
            this.field_149012_f = packetbuffer.readShort();
            this.field_149013_g = packetbuffer.readShort();
        }

    }

    @Override
    public void func_148840_b(PacketBuffer packetbuffer) {
        packetbuffer.func_150787_b(this.field_149018_a);
        packetbuffer.writeByte(this.field_149019_j);
        packetbuffer.writeInt(this.field_149016_b);
        packetbuffer.writeInt(this.field_149017_c);
        packetbuffer.writeInt(this.field_149014_d);
        packetbuffer.writeByte(this.field_149021_h);
        packetbuffer.writeByte(this.field_149022_i);
        packetbuffer.writeInt(this.field_149020_k);
        if (this.field_149020_k > 0) {
            packetbuffer.writeShort(this.field_149015_e);
            packetbuffer.writeShort(this.field_149012_f);
            packetbuffer.writeShort(this.field_149013_g);
        }

    }

    public void func_148833_a(INetHandlerPlayClient inethandlerplayclient) {
        inethandlerplayclient.func_147235_a(this);
    }

    @Override
    public String func_148835_b() {
        return String.format("id=%d, type=%d, x=%.2f, y=%.2f, z=%.2f", this.field_149018_a, this.field_149019_j, (float) this.field_149016_b / 32.0F, (float) this.field_149017_c / 32.0F, (float) this.field_149014_d / 32.0F);
    }

    public void func_148996_a(int i) {
        this.field_149016_b = i;
    }

    public void func_148995_b(int i) {
        this.field_149017_c = i;
    }

    public void func_149005_c(int i) {
        this.field_149014_d = i;
    }

    public void func_149003_d(int i) {
        this.field_149015_e = i;
    }

    public void func_149000_e(int i) {
        this.field_149012_f = i;
    }

    public void func_149007_f(int i) {
        this.field_149013_g = i;
    }
}
