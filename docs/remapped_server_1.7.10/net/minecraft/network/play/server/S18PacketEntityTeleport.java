package net.minecraft.network.play.server;

import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.MathHelper;

public class S18PacketEntityTeleport extends Packet {

    private int field_149458_a;
    private int field_149456_b;
    private int field_149457_c;
    private int field_149454_d;
    private byte field_149455_e;
    private byte field_149453_f;

    public S18PacketEntityTeleport() {}

    public S18PacketEntityTeleport(Entity entity) {
        this.field_149458_a = entity.func_145782_y();
        this.field_149456_b = MathHelper.func_76128_c(entity.field_70165_t * 32.0D);
        this.field_149457_c = MathHelper.func_76128_c(entity.field_70163_u * 32.0D);
        this.field_149454_d = MathHelper.func_76128_c(entity.field_70161_v * 32.0D);
        this.field_149455_e = (byte) ((int) (entity.field_70177_z * 256.0F / 360.0F));
        this.field_149453_f = (byte) ((int) (entity.field_70125_A * 256.0F / 360.0F));
    }

    public S18PacketEntityTeleport(int i, int j, int k, int l, byte b0, byte b1) {
        this.field_149458_a = i;
        this.field_149456_b = j;
        this.field_149457_c = k;
        this.field_149454_d = l;
        this.field_149455_e = b0;
        this.field_149453_f = b1;
    }

    @Override
    public void func_148837_a(PacketBuffer packetbuffer) {
        this.field_149458_a = packetbuffer.readInt();
        this.field_149456_b = packetbuffer.readInt();
        this.field_149457_c = packetbuffer.readInt();
        this.field_149454_d = packetbuffer.readInt();
        this.field_149455_e = packetbuffer.readByte();
        this.field_149453_f = packetbuffer.readByte();
    }

    @Override
    public void func_148840_b(PacketBuffer packetbuffer) {
        packetbuffer.writeInt(this.field_149458_a);
        packetbuffer.writeInt(this.field_149456_b);
        packetbuffer.writeInt(this.field_149457_c);
        packetbuffer.writeInt(this.field_149454_d);
        packetbuffer.writeByte(this.field_149455_e);
        packetbuffer.writeByte(this.field_149453_f);
    }

    public void func_148833_a(INetHandlerPlayClient inethandlerplayclient) {
        inethandlerplayclient.func_147275_a(this);
    }
}
