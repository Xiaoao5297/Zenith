package net.minecraft.network.handshake.client;

import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.handshake.INetHandlerHandshakeServer;

public class C00Handshake extends Packet {

    private int field_149600_a;
    private String field_149598_b;
    private int field_149599_c;
    private EnumConnectionState field_149597_d;

    public C00Handshake() {}

    @Override
    public void func_148837_a(PacketBuffer packetbuffer) {
        this.field_149600_a = packetbuffer.func_150792_a();
        this.field_149598_b = packetbuffer.func_150789_c(255);
        this.field_149599_c = packetbuffer.readUnsignedShort();
        this.field_149597_d = EnumConnectionState.func_150760_a(packetbuffer.func_150792_a());
    }

    @Override
    public void func_148840_b(PacketBuffer packetbuffer) {
        packetbuffer.func_150787_b(this.field_149600_a);
        packetbuffer.func_150785_a(this.field_149598_b);
        packetbuffer.writeShort(this.field_149599_c);
        packetbuffer.func_150787_b(this.field_149597_d.func_150759_c());
    }

    public void func_148833_a(INetHandlerHandshakeServer inethandlerhandshakeserver) {
        inethandlerhandshakeserver.func_147383_a(this);
    }

    @Override
    public boolean func_148836_a() {
        return true;
    }

    public EnumConnectionState func_149594_c() {
        return this.field_149597_d;
    }

    public int func_149595_d() {
        return this.field_149600_a;
    }
}
