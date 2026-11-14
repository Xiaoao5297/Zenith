package net.minecraft.network.play.client;

import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraft.world.World;

public class C02PacketUseEntity extends Packet {

    private int field_149567_a;
    private C02PacketUseEntity$Action field_149566_b;

    public C02PacketUseEntity() {}

    @Override
    public void func_148837_a(PacketBuffer packetbuffer) {
        this.field_149567_a = packetbuffer.readInt();
        this.field_149566_b = C02PacketUseEntity$Action.access$000()[packetbuffer.readByte() % C02PacketUseEntity$Action.access$000().length];
    }

    @Override
    public void func_148840_b(PacketBuffer packetbuffer) {
        packetbuffer.writeInt(this.field_149567_a);
        packetbuffer.writeByte(C02PacketUseEntity$Action.access$100(this.field_149566_b));
    }

    public void func_148833_a(INetHandlerPlayServer inethandlerplayserver) {
        inethandlerplayserver.func_147340_a(this);
    }

    public Entity func_149564_a(World world) {
        return world.func_73045_a(this.field_149567_a);
    }

    public C02PacketUseEntity$Action func_149565_c() {
        return this.field_149566_b;
    }
}
