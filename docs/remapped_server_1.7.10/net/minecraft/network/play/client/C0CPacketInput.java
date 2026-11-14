package net.minecraft.network.play.client;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;

public class C0CPacketInput extends Packet {

    private float field_149624_a;
    private float field_149622_b;
    private boolean field_149623_c;
    private boolean field_149621_d;

    public C0CPacketInput() {}

    @Override
    public void func_148837_a(PacketBuffer packetbuffer) {
        this.field_149624_a = packetbuffer.readFloat();
        this.field_149622_b = packetbuffer.readFloat();
        this.field_149623_c = packetbuffer.readBoolean();
        this.field_149621_d = packetbuffer.readBoolean();
    }

    @Override
    public void func_148840_b(PacketBuffer packetbuffer) {
        packetbuffer.writeFloat(this.field_149624_a);
        packetbuffer.writeFloat(this.field_149622_b);
        packetbuffer.writeBoolean(this.field_149623_c);
        packetbuffer.writeBoolean(this.field_149621_d);
    }

    public void func_148833_a(INetHandlerPlayServer inethandlerplayserver) {
        inethandlerplayserver.func_147358_a(this);
    }

    public float func_149620_c() {
        return this.field_149624_a;
    }

    public float func_149616_d() {
        return this.field_149622_b;
    }

    public boolean func_149618_e() {
        return this.field_149623_c;
    }

    public boolean func_149617_f() {
        return this.field_149621_d;
    }
}
