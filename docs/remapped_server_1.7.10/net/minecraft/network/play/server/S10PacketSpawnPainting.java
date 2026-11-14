package net.minecraft.network.play.server;

import net.minecraft.entity.item.EntityPainting;
import net.minecraft.entity.item.EntityPainting$EnumArt;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S10PacketSpawnPainting extends Packet {

    private int field_148973_a;
    private int field_148971_b;
    private int field_148972_c;
    private int field_148969_d;
    private int field_148970_e;
    private String field_148968_f;

    public S10PacketSpawnPainting() {}

    public S10PacketSpawnPainting(EntityPainting entitypainting) {
        this.field_148973_a = entitypainting.func_145782_y();
        this.field_148971_b = entitypainting.field_146063_b;
        this.field_148972_c = entitypainting.field_146064_c;
        this.field_148969_d = entitypainting.field_146062_d;
        this.field_148970_e = entitypainting.field_82332_a;
        this.field_148968_f = entitypainting.field_70522_e.field_75702_A;
    }

    @Override
    public void func_148837_a(PacketBuffer packetbuffer) {
        this.field_148973_a = packetbuffer.func_150792_a();
        this.field_148968_f = packetbuffer.func_150789_c(EntityPainting$EnumArt.field_75728_z);
        this.field_148971_b = packetbuffer.readInt();
        this.field_148972_c = packetbuffer.readInt();
        this.field_148969_d = packetbuffer.readInt();
        this.field_148970_e = packetbuffer.readInt();
    }

    @Override
    public void func_148840_b(PacketBuffer packetbuffer) {
        packetbuffer.func_150787_b(this.field_148973_a);
        packetbuffer.func_150785_a(this.field_148968_f);
        packetbuffer.writeInt(this.field_148971_b);
        packetbuffer.writeInt(this.field_148972_c);
        packetbuffer.writeInt(this.field_148969_d);
        packetbuffer.writeInt(this.field_148970_e);
    }

    public void func_148833_a(INetHandlerPlayClient inethandlerplayclient) {
        inethandlerplayclient.func_147288_a(this);
    }

    @Override
    public String func_148835_b() {
        return String.format("id=%d, type=%s, x=%d, y=%d, z=%d", this.field_148973_a, this.field_148968_f, this.field_148971_b, this.field_148972_c, this.field_148969_d);
    }
}
