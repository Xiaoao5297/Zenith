package net.minecraft.network.play.server;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.potion.PotionEffect;

public class S1DPacketEntityEffect extends Packet {

    private int field_149434_a;
    private byte field_149432_b;
    private byte field_149433_c;
    private short field_149431_d;

    public S1DPacketEntityEffect() {}

    public S1DPacketEntityEffect(int i, PotionEffect potioneffect) {
        this.field_149434_a = i;
        this.field_149432_b = (byte) (potioneffect.func_76456_a() & 255);
        this.field_149433_c = (byte) (potioneffect.func_76458_c() & 255);
        if (potioneffect.func_76459_b() > 32767) {
            this.field_149431_d = 32767;
        } else {
            this.field_149431_d = (short) potioneffect.func_76459_b();
        }

    }

    @Override
    public void func_148837_a(PacketBuffer packetbuffer) {
        this.field_149434_a = packetbuffer.readInt();
        this.field_149432_b = packetbuffer.readByte();
        this.field_149433_c = packetbuffer.readByte();
        this.field_149431_d = packetbuffer.readShort();
    }

    @Override
    public void func_148840_b(PacketBuffer packetbuffer) {
        packetbuffer.writeInt(this.field_149434_a);
        packetbuffer.writeByte(this.field_149432_b);
        packetbuffer.writeByte(this.field_149433_c);
        packetbuffer.writeShort(this.field_149431_d);
    }

    public void func_148833_a(INetHandlerPlayClient inethandlerplayclient) {
        inethandlerplayclient.func_147260_a(this);
    }
}
