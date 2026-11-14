package net.minecraft.network.play.server;

import java.util.List;
import net.minecraft.entity.DataWatcher;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.MathHelper;

public class S0FPacketSpawnMob extends Packet {

    private int field_149042_a;
    private int field_149040_b;
    private int field_149041_c;
    private int field_149038_d;
    private int field_149039_e;
    private int field_149036_f;
    private int field_149037_g;
    private int field_149047_h;
    private byte field_149048_i;
    private byte field_149045_j;
    private byte field_149046_k;
    private DataWatcher field_149043_l;
    private List field_149044_m;

    public S0FPacketSpawnMob() {}

    public S0FPacketSpawnMob(EntityLivingBase entitylivingbase) {
        this.field_149042_a = entitylivingbase.func_145782_y();
        this.field_149040_b = (byte) EntityList.func_75619_a(entitylivingbase);
        this.field_149041_c = entitylivingbase.field_70168_am.func_75630_a(entitylivingbase.field_70165_t);
        this.field_149038_d = MathHelper.func_76128_c(entitylivingbase.field_70163_u * 32.0D);
        this.field_149039_e = entitylivingbase.field_70168_am.func_75630_a(entitylivingbase.field_70161_v);
        this.field_149048_i = (byte) ((int) (entitylivingbase.field_70177_z * 256.0F / 360.0F));
        this.field_149045_j = (byte) ((int) (entitylivingbase.field_70125_A * 256.0F / 360.0F));
        this.field_149046_k = (byte) ((int) (entitylivingbase.field_70759_as * 256.0F / 360.0F));
        double d0 = 3.9D;
        double d1 = entitylivingbase.field_70159_w;
        double d2 = entitylivingbase.field_70181_x;
        double d3 = entitylivingbase.field_70179_y;

        if (d1 < -d0) {
            d1 = -d0;
        }

        if (d2 < -d0) {
            d2 = -d0;
        }

        if (d3 < -d0) {
            d3 = -d0;
        }

        if (d1 > d0) {
            d1 = d0;
        }

        if (d2 > d0) {
            d2 = d0;
        }

        if (d3 > d0) {
            d3 = d0;
        }

        this.field_149036_f = (int) (d1 * 8000.0D);
        this.field_149037_g = (int) (d2 * 8000.0D);
        this.field_149047_h = (int) (d3 * 8000.0D);
        this.field_149043_l = entitylivingbase.func_70096_w();
    }

    @Override
    public void func_148837_a(PacketBuffer packetbuffer) {
        this.field_149042_a = packetbuffer.func_150792_a();
        this.field_149040_b = packetbuffer.readByte() & 255;
        this.field_149041_c = packetbuffer.readInt();
        this.field_149038_d = packetbuffer.readInt();
        this.field_149039_e = packetbuffer.readInt();
        this.field_149048_i = packetbuffer.readByte();
        this.field_149045_j = packetbuffer.readByte();
        this.field_149046_k = packetbuffer.readByte();
        this.field_149036_f = packetbuffer.readShort();
        this.field_149037_g = packetbuffer.readShort();
        this.field_149047_h = packetbuffer.readShort();
        this.field_149044_m = DataWatcher.func_151508_b(packetbuffer);
    }

    @Override
    public void func_148840_b(PacketBuffer packetbuffer) {
        packetbuffer.func_150787_b(this.field_149042_a);
        packetbuffer.writeByte(this.field_149040_b & 255);
        packetbuffer.writeInt(this.field_149041_c);
        packetbuffer.writeInt(this.field_149038_d);
        packetbuffer.writeInt(this.field_149039_e);
        packetbuffer.writeByte(this.field_149048_i);
        packetbuffer.writeByte(this.field_149045_j);
        packetbuffer.writeByte(this.field_149046_k);
        packetbuffer.writeShort(this.field_149036_f);
        packetbuffer.writeShort(this.field_149037_g);
        packetbuffer.writeShort(this.field_149047_h);
        this.field_149043_l.func_151509_a(packetbuffer);
    }

    public void func_148833_a(INetHandlerPlayClient inethandlerplayclient) {
        inethandlerplayclient.func_147281_a(this);
    }

    @Override
    public String func_148835_b() {
        return String.format("id=%d, type=%d, x=%.2f, y=%.2f, z=%.2f, xd=%.2f, yd=%.2f, zd=%.2f", this.field_149042_a, this.field_149040_b, (float) this.field_149041_c / 32.0F, (float) this.field_149038_d / 32.0F, (float) this.field_149039_e / 32.0F, (float) this.field_149036_f / 8000.0F, (float) this.field_149037_g / 8000.0F, (float) this.field_149047_h / 8000.0F);
    }
}
