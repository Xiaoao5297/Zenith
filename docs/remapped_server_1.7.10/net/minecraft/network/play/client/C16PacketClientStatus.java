package net.minecraft.network.play.client;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;

public class C16PacketClientStatus extends Packet {

    private C16PacketClientStatus$EnumState field_149437_a;

    public C16PacketClientStatus() {}

    public C16PacketClientStatus(C16PacketClientStatus$EnumState c16packetclientstatus$enumstate) {
        this.field_149437_a = c16packetclientstatus$enumstate;
    }

    @Override
    public void func_148837_a(PacketBuffer packetbuffer) {
        this.field_149437_a = C16PacketClientStatus$EnumState.access$000()[packetbuffer.readByte() % C16PacketClientStatus$EnumState.access$000().length];
    }

    @Override
    public void func_148840_b(PacketBuffer packetbuffer) {
        packetbuffer.writeByte(C16PacketClientStatus$EnumState.access$100(this.field_149437_a));
    }

    public void func_148833_a(INetHandlerPlayServer inethandlerplayserver) {
        inethandlerplayserver.func_147342_a(this);
    }

    public C16PacketClientStatus$EnumState func_149435_c() {
        return this.field_149437_a;
    }
}
