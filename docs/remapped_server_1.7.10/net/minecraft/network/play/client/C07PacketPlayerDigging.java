package net.minecraft.network.play.client;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;

public class C07PacketPlayerDigging extends Packet {

    private int field_149511_a;
    private int field_149509_b;
    private int field_149510_c;
    private int field_149507_d;
    private int field_149508_e;

    public C07PacketPlayerDigging() {}

    @Override
    public void func_148837_a(PacketBuffer packetbuffer) {
        this.field_149508_e = packetbuffer.readUnsignedByte();
        this.field_149511_a = packetbuffer.readInt();
        this.field_149509_b = packetbuffer.readUnsignedByte();
        this.field_149510_c = packetbuffer.readInt();
        this.field_149507_d = packetbuffer.readUnsignedByte();
    }

    @Override
    public void func_148840_b(PacketBuffer packetbuffer) {
        packetbuffer.writeByte(this.field_149508_e);
        packetbuffer.writeInt(this.field_149511_a);
        packetbuffer.writeByte(this.field_149509_b);
        packetbuffer.writeInt(this.field_149510_c);
        packetbuffer.writeByte(this.field_149507_d);
    }

    public void func_148833_a(INetHandlerPlayServer inethandlerplayserver) {
        inethandlerplayserver.func_147345_a(this);
    }

    public int func_149505_c() {
        return this.field_149511_a;
    }

    public int func_149503_d() {
        return this.field_149509_b;
    }

    public int func_149502_e() {
        return this.field_149510_c;
    }

    public int func_149501_f() {
        return this.field_149507_d;
    }

    public int func_149506_g() {
        return this.field_149508_e;
    }
}
