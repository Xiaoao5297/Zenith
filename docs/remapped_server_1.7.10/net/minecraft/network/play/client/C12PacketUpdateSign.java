package net.minecraft.network.play.client;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;

public class C12PacketUpdateSign extends Packet {

    private int field_149593_a;
    private int field_149591_b;
    private int field_149592_c;
    private String[] field_149590_d;

    public C12PacketUpdateSign() {}

    @Override
    public void func_148837_a(PacketBuffer packetbuffer) {
        this.field_149593_a = packetbuffer.readInt();
        this.field_149591_b = packetbuffer.readShort();
        this.field_149592_c = packetbuffer.readInt();
        this.field_149590_d = new String[4];

        for (int i = 0; i < 4; ++i) {
            this.field_149590_d[i] = packetbuffer.func_150789_c(15);
        }

    }

    @Override
    public void func_148840_b(PacketBuffer packetbuffer) {
        packetbuffer.writeInt(this.field_149593_a);
        packetbuffer.writeShort(this.field_149591_b);
        packetbuffer.writeInt(this.field_149592_c);

        for (int i = 0; i < 4; ++i) {
            packetbuffer.func_150785_a(this.field_149590_d[i]);
        }

    }

    public void func_148833_a(INetHandlerPlayServer inethandlerplayserver) {
        inethandlerplayserver.func_147343_a(this);
    }

    public int func_149588_c() {
        return this.field_149593_a;
    }

    public int func_149586_d() {
        return this.field_149591_b;
    }

    public int func_149585_e() {
        return this.field_149592_c;
    }

    public String[] func_149589_f() {
        return this.field_149590_d;
    }
}
