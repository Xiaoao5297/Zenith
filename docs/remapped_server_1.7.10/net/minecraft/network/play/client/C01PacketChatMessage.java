package net.minecraft.network.play.client;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;

public class C01PacketChatMessage extends Packet {

    private String field_149440_a;

    public C01PacketChatMessage() {}

    public C01PacketChatMessage(String s) {
        if (s.length() > 100) {
            s = s.substring(0, 100);
        }

        this.field_149440_a = s;
    }

    @Override
    public void func_148837_a(PacketBuffer packetbuffer) {
        this.field_149440_a = packetbuffer.func_150789_c(100);
    }

    @Override
    public void func_148840_b(PacketBuffer packetbuffer) {
        packetbuffer.func_150785_a(this.field_149440_a);
    }

    public void func_148833_a(INetHandlerPlayServer inethandlerplayserver) {
        inethandlerplayserver.func_147354_a(this);
    }

    @Override
    public String func_148835_b() {
        return String.format("message='%s'", this.field_149440_a);
    }

    public String func_149439_c() {
        return this.field_149440_a;
    }
}
