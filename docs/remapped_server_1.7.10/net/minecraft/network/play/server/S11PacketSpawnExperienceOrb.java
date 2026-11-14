package net.minecraft.network.play.server;

import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.MathHelper;

public class S11PacketSpawnExperienceOrb extends Packet {

    private int field_148992_a;
    private int field_148990_b;
    private int field_148991_c;
    private int field_148988_d;
    private int field_148989_e;

    public S11PacketSpawnExperienceOrb() {}

    public S11PacketSpawnExperienceOrb(EntityXPOrb entityxporb) {
        this.field_148992_a = entityxporb.func_145782_y();
        this.field_148990_b = MathHelper.func_76128_c(entityxporb.field_70165_t * 32.0D);
        this.field_148991_c = MathHelper.func_76128_c(entityxporb.field_70163_u * 32.0D);
        this.field_148988_d = MathHelper.func_76128_c(entityxporb.field_70161_v * 32.0D);
        this.field_148989_e = entityxporb.func_70526_d();
    }

    @Override
    public void func_148837_a(PacketBuffer packetbuffer) {
        this.field_148992_a = packetbuffer.func_150792_a();
        this.field_148990_b = packetbuffer.readInt();
        this.field_148991_c = packetbuffer.readInt();
        this.field_148988_d = packetbuffer.readInt();
        this.field_148989_e = packetbuffer.readShort();
    }

    @Override
    public void func_148840_b(PacketBuffer packetbuffer) {
        packetbuffer.func_150787_b(this.field_148992_a);
        packetbuffer.writeInt(this.field_148990_b);
        packetbuffer.writeInt(this.field_148991_c);
        packetbuffer.writeInt(this.field_148988_d);
        packetbuffer.writeShort(this.field_148989_e);
    }

    public void func_148833_a(INetHandlerPlayClient inethandlerplayclient) {
        inethandlerplayclient.func_147286_a(this);
    }

    @Override
    public String func_148835_b() {
        return String.format("id=%d, value=%d, x=%.2f, y=%.2f, z=%.2f", this.field_148992_a, this.field_148989_e, (float) this.field_148990_b / 32.0F, (float) this.field_148991_c / 32.0F, (float) this.field_148988_d / 32.0F);
    }
}
