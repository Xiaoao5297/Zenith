package net.minecraft.network.play.server;

import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.MathHelper;

public class S2CPacketSpawnGlobalEntity extends Packet {

    private int field_149059_a;
    private int field_149057_b;
    private int field_149058_c;
    private int field_149055_d;
    private int field_149056_e;

    public S2CPacketSpawnGlobalEntity() {}

    public S2CPacketSpawnGlobalEntity(Entity entity) {
        this.field_149059_a = entity.func_145782_y();
        this.field_149057_b = MathHelper.func_76128_c(entity.field_70165_t * 32.0D);
        this.field_149058_c = MathHelper.func_76128_c(entity.field_70163_u * 32.0D);
        this.field_149055_d = MathHelper.func_76128_c(entity.field_70161_v * 32.0D);
        if (entity instanceof EntityLightningBolt) {
            this.field_149056_e = 1;
        }

    }

    @Override
    public void func_148837_a(PacketBuffer packetbuffer) {
        this.field_149059_a = packetbuffer.func_150792_a();
        this.field_149056_e = packetbuffer.readByte();
        this.field_149057_b = packetbuffer.readInt();
        this.field_149058_c = packetbuffer.readInt();
        this.field_149055_d = packetbuffer.readInt();
    }

    @Override
    public void func_148840_b(PacketBuffer packetbuffer) {
        packetbuffer.func_150787_b(this.field_149059_a);
        packetbuffer.writeByte(this.field_149056_e);
        packetbuffer.writeInt(this.field_149057_b);
        packetbuffer.writeInt(this.field_149058_c);
        packetbuffer.writeInt(this.field_149055_d);
    }

    public void func_148833_a(INetHandlerPlayClient inethandlerplayclient) {
        inethandlerplayclient.func_147292_a(this);
    }

    @Override
    public String func_148835_b() {
        return String.format("id=%d, type=%d, x=%.2f, y=%.2f, z=%.2f", this.field_149059_a, this.field_149056_e, (float) this.field_149057_b / 32.0F, (float) this.field_149058_c / 32.0F, (float) this.field_149055_d / 32.0F);
    }
}
