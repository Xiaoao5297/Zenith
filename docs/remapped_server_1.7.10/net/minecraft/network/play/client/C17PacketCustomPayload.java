package net.minecraft.network.play.client;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;

public class C17PacketCustomPayload extends Packet {

    private String field_149562_a;
    private int field_149560_b;
    private byte[] field_149561_c;

    public C17PacketCustomPayload() {}

    @Override
    public void func_148837_a(PacketBuffer packetbuffer) {
        this.field_149562_a = packetbuffer.func_150789_c(20);
        this.field_149560_b = packetbuffer.readShort();
        if (this.field_149560_b > 0 && this.field_149560_b < 32767) {
            this.field_149561_c = new byte[this.field_149560_b];
            packetbuffer.readBytes(this.field_149561_c);
        }

    }

    @Override
    public void func_148840_b(PacketBuffer packetbuffer) {
        packetbuffer.func_150785_a(this.field_149562_a);
        packetbuffer.writeShort((short) this.field_149560_b);
        if (this.field_149561_c != null) {
            packetbuffer.writeBytes(this.field_149561_c);
        }

    }

    public void func_148833_a(INetHandlerPlayServer inethandlerplayserver) {
        inethandlerplayserver.func_147349_a(this);
    }

    public String func_149559_c() {
        return this.field_149562_a;
    }

    public byte[] func_149558_e() {
        return this.field_149561_c;
    }
}
