package net.minecraft.network.play.server;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.potion.PotionEffect;

public class S1EPacketRemoveEntityEffect extends Packet {

    private int field_149079_a;
    private int field_149078_b;

    public S1EPacketRemoveEntityEffect() {}

    public S1EPacketRemoveEntityEffect(int i, PotionEffect potioneffect) {
        this.field_149079_a = i;
        this.field_149078_b = potioneffect.func_76456_a();
    }

    @Override
    public void func_148837_a(PacketBuffer packetbuffer) {
        this.field_149079_a = packetbuffer.readInt();
        this.field_149078_b = packetbuffer.readUnsignedByte();
    }

    @Override
    public void func_148840_b(PacketBuffer packetbuffer) {
        packetbuffer.writeInt(this.field_149079_a);
        packetbuffer.writeByte(this.field_149078_b);
    }

    public void func_148833_a(INetHandlerPlayClient inethandlerplayclient) {
        inethandlerplayclient.func_147262_a(this);
    }
}
